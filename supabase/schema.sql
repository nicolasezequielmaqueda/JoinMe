-- JoinMe database schema.
-- Safe to run more than once: tables use "if not exists" and functions/policies are recreated.
-- Note: if a table already exists, its CHECK constraints are not changed by re-running this file.

-- ---------------------------------------------------------------------------
-- Tables
-- ---------------------------------------------------------------------------

-- The allowed values of type and location must match ActivityType and ResidenceLocation in the app.
create table if not exists public.activities (
    id                uuid primary key default gen_random_uuid(),
    type              text not null,
    custom_name       text not null default '',
    date              date not null,
    start_time        time not null,
    duration_minutes  integer not null,
    location          text not null,
    max_participants  integer not null,
    description       text not null default '',
    creator_id        uuid not null references auth.users (id) on delete cascade,
    is_cancelled      boolean not null default false,
    is_demo           boolean not null default false,
    created_at        timestamptz not null default now(),

    constraint activities_type_check check (type in (
        'FOOTBALL', 'PADEL', 'BADMINTON', 'TABLE_TENNIS', 'POOL', 'TABLE_FOOTBALL',
        'DARTS', 'BOARD_GAMES', 'CARDS', 'MOVIE_NIGHT', 'OTHER'
    )),
    constraint activities_location_check check (location in (
        'COMMON_ROOM', 'GAMES_ROOM', 'SPORTS_COURT', 'CINEMA_ROOM', 'GARDEN', 'ENTRANCE', 'OTHER'
    )),
    constraint activities_custom_name_check check (char_length(custom_name) <= 30),
    constraint activities_description_check check (char_length(description) <= 150),
    constraint activities_duration_check check (duration_minutes > 0),
    constraint activities_max_participants_check check (max_participants between 2 and 30),
    -- An "Other" activity has no predefined name, so it needs one.
    constraint activities_other_needs_name_check check (type <> 'OTHER' or char_length(btrim(custom_name, E' \t\n\r')) > 0)
);

create table if not exists public.participants (
    activity_id  uuid not null references public.activities (id) on delete cascade,
    user_id      uuid not null references auth.users (id) on delete cascade,
    joined_at    timestamptz not null default now(),

    primary key (activity_id, user_id)
);

-- Postgres does not index foreign keys by itself; these speed up cascades and the usual filters.
create index if not exists activities_creator_id_idx on public.activities (creator_id);
create index if not exists activities_date_idx on public.activities (date);
-- The primary key already covers lookups by activity_id.
create index if not exists participants_user_id_idx on public.participants (user_id);

-- ---------------------------------------------------------------------------
-- Row Level Security and table grants
-- ---------------------------------------------------------------------------

alter table public.activities enable row level security;
alter table public.participants enable row level security;

-- The project does not grant API access automatically, so everything is explicit.
-- Nobody writes to the tables directly: all changes go through the functions below.
revoke all on public.activities, public.participants from anon, authenticated;
grant select on public.activities, public.participants to authenticated;

drop policy if exists "activities_select_authenticated" on public.activities;
create policy "activities_select_authenticated"
    on public.activities for select
    to authenticated
    using (true);

drop policy if exists "participants_select_authenticated" on public.participants;
create policy "participants_select_authenticated"
    on public.participants for select
    to authenticated
    using (true);

-- ---------------------------------------------------------------------------
-- Helper
-- ---------------------------------------------------------------------------

-- Same rule as ActivityRules.endOf: finished when start + duration <= now.
-- The date and time are local residence time, so "now" is converted to Warsaw time.
create or replace function public.activity_has_ended(
    p_date date,
    p_start_time time,
    p_duration_minutes integer
)
returns boolean
language sql
stable
set search_path = ''
as $$
    select (p_date + p_start_time + make_interval(mins => p_duration_minutes))
           <= (now() at time zone 'Europe/Warsaw');
$$;

-- ---------------------------------------------------------------------------
-- Functions called by the app
-- They are security definer (no direct table writes for users), so each one
-- checks auth.uid() itself and never trusts a user id sent by the client.
-- Errors are plain text codes in the exception message, e.g. 'FULL'.
-- ---------------------------------------------------------------------------

-- Errors: NOT_AUTHENTICATED, NOT_FOUND, ALREADY_JOINED, CANCELLED, COMPLETED, FULL (same order as ActivityRules.join).
create or replace function public.join_activity(p_activity_id uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_uid uuid := auth.uid();
    v_activity public.activities;
begin
    if v_uid is null then
        raise exception 'NOT_AUTHENTICATED';
    end if;

    -- The row lock makes two simultaneous joins count the participants one after the other.
    select * into v_activity from public.activities where id = p_activity_id for update;
    if not found then
        raise exception 'NOT_FOUND';
    end if;

    if exists (
        select 1 from public.participants
        where activity_id = p_activity_id and user_id = v_uid
    ) then
        raise exception 'ALREADY_JOINED';
    end if;

    if v_activity.is_cancelled then
        raise exception 'CANCELLED';
    end if;

    if public.activity_has_ended(v_activity.date, v_activity.start_time, v_activity.duration_minutes) then
        raise exception 'COMPLETED';
    end if;

    if (select count(*) from public.participants where activity_id = p_activity_id) >= v_activity.max_participants then
        raise exception 'FULL';
    end if;

    insert into public.participants (activity_id, user_id) values (p_activity_id, v_uid);
end;
$$;

-- Errors: NOT_AUTHENTICATED, NOT_FOUND, NOT_JOINED, CREATOR_CANNOT_LEAVE, COMPLETED (same order as ActivityRules.leave).
create or replace function public.leave_activity(p_activity_id uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_uid uuid := auth.uid();
    v_activity public.activities;
begin
    if v_uid is null then
        raise exception 'NOT_AUTHENTICATED';
    end if;

    select * into v_activity from public.activities where id = p_activity_id for update;
    if not found then
        raise exception 'NOT_FOUND';
    end if;

    if not exists (
        select 1 from public.participants
        where activity_id = p_activity_id and user_id = v_uid
    ) then
        raise exception 'NOT_JOINED';
    end if;

    if v_activity.creator_id = v_uid then
        raise exception 'CREATOR_CANNOT_LEAVE';
    end if;

    -- Like the app: a cancelled activity is shown as CANCELLED, so leaving it is still allowed.
    if not v_activity.is_cancelled
       and public.activity_has_ended(v_activity.date, v_activity.start_time, v_activity.duration_minutes) then
        raise exception 'COMPLETED';
    end if;

    delete from public.participants where activity_id = p_activity_id and user_id = v_uid;
end;
$$;

-- Errors: NOT_AUTHENTICATED, INVALID_TYPE, NAME_REQUIRED, NAME_TOO_LONG, INVALID_START, PAST_START,
-- INVALID_DURATION, INVALID_LOCATION, INVALID_PARTICIPANTS, DESCRIPTION_TOO_LONG (same order as ActivityValidator).
-- Returns the id of the new activity; the creator is added as the first participant.
create or replace function public.create_activity(
    p_type text,
    p_date date,
    p_start_time time,
    p_duration_minutes integer,
    p_location text,
    p_max_participants integer,
    p_custom_name text default '',
    p_description text default ''
)
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_uid uuid := auth.uid();
    v_custom_name text := coalesce(p_custom_name, '');
    v_description text := coalesce(p_description, '');
    v_id uuid;
begin
    if v_uid is null then
        raise exception 'NOT_AUTHENTICATED';
    end if;

    if p_type is null or p_type not in (
        'FOOTBALL', 'PADEL', 'BADMINTON', 'TABLE_TENNIS', 'POOL', 'TABLE_FOOTBALL',
        'DARTS', 'BOARD_GAMES', 'CARDS', 'MOVIE_NIGHT', 'OTHER'
    ) then
        raise exception 'INVALID_TYPE';
    end if;

    if p_type = 'OTHER' and btrim(v_custom_name, E' \t\n\r') = '' then
        raise exception 'NAME_REQUIRED';
    end if;

    if char_length(v_custom_name) > 30 then
        raise exception 'NAME_TOO_LONG';
    end if;

    if p_date is null or p_start_time is null then
        raise exception 'INVALID_START';
    end if;

    if (p_date + p_start_time) <= (now() at time zone 'Europe/Warsaw') then
        raise exception 'PAST_START';
    end if;

    if p_duration_minutes is null or p_duration_minutes <= 0 then
        raise exception 'INVALID_DURATION';
    end if;

    if p_location is null or p_location not in (
        'COMMON_ROOM', 'GAMES_ROOM', 'SPORTS_COURT', 'CINEMA_ROOM', 'GARDEN', 'ENTRANCE', 'OTHER'
    ) then
        raise exception 'INVALID_LOCATION';
    end if;

    if p_max_participants is null or p_max_participants not between 2 and 30 then
        raise exception 'INVALID_PARTICIPANTS';
    end if;

    if char_length(v_description) > 150 then
        raise exception 'DESCRIPTION_TOO_LONG';
    end if;

    insert into public.activities (
        type, custom_name, date, start_time, duration_minutes,
        location, max_participants, description, creator_id
    )
    values (
        p_type, v_custom_name, p_date, p_start_time, p_duration_minutes,
        p_location, p_max_participants, v_description, v_uid
    )
    returning id into v_id;

    insert into public.participants (activity_id, user_id) values (v_id, v_uid);

    return v_id;
end;
$$;

-- Errors: NOT_AUTHENTICATED, NOT_FOUND, NOT_CREATOR, ALREADY_CANCELLED, COMPLETED (same order as ActivityRules.cancel).
-- It is a function (not a plain UPDATE) because a finished activity must not be cancellable,
-- and a table UPDATE grant could not enforce that.
create or replace function public.cancel_activity(p_activity_id uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_uid uuid := auth.uid();
    v_activity public.activities;
begin
    if v_uid is null then
        raise exception 'NOT_AUTHENTICATED';
    end if;

    select * into v_activity from public.activities where id = p_activity_id for update;
    if not found then
        raise exception 'NOT_FOUND';
    end if;

    if v_activity.creator_id <> v_uid then
        raise exception 'NOT_CREATOR';
    end if;

    if v_activity.is_cancelled then
        raise exception 'ALREADY_CANCELLED';
    end if;

    if public.activity_has_ended(v_activity.date, v_activity.start_time, v_activity.duration_minutes) then
        raise exception 'COMPLETED';
    end if;

    update public.activities set is_cancelled = true where id = p_activity_id;
end;
$$;

-- Errors: NOT_AUTHENTICATED.
-- Deleting the auth user cascades to the user's activities and participations.
create or replace function public.delete_my_account()
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_uid uuid := auth.uid();
begin
    if v_uid is null then
        raise exception 'NOT_AUTHENTICATED';
    end if;

    delete from auth.users where id = v_uid;
end;
$$;

-- ---------------------------------------------------------------------------
-- Function permissions
-- New functions are executable by PUBLIC (and by anon/authenticated through Supabase's
-- default privileges), so revoke first and then grant only what the app calls.
-- ---------------------------------------------------------------------------

revoke execute on function public.activity_has_ended(date, time, integer) from public, anon, authenticated;
revoke execute on function public.join_activity(uuid) from public, anon, authenticated;
revoke execute on function public.leave_activity(uuid) from public, anon, authenticated;
revoke execute on function public.create_activity(text, date, time, integer, text, integer, text, text) from public, anon, authenticated;
revoke execute on function public.cancel_activity(uuid) from public, anon, authenticated;
revoke execute on function public.delete_my_account() from public, anon, authenticated;

-- activity_has_ended is only used inside the other functions, so it is not granted.
grant execute on function public.join_activity(uuid) to authenticated;
grant execute on function public.leave_activity(uuid) to authenticated;
grant execute on function public.create_activity(text, date, time, integer, text, integer, text, text) to authenticated;
grant execute on function public.cancel_activity(uuid) to authenticated;
grant execute on function public.delete_my_account() to authenticated;

-- rls_auto_enable is created by Supabase when "Enable RLS automatically" is on.
-- It runs from an event trigger, so the API roles never need to call it.
do $$
begin
  if to_regprocedure('public.rls_auto_enable()') is not null then
    revoke execute on function public.rls_auto_enable() from public, anon, authenticated;
  end if;
end $$;
