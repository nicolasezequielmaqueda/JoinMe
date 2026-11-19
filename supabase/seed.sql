-- JoinMe demo data (fictional). Every row is marked is_demo = true.
-- Run it as the postgres role (SQL editor or Management API); it bypasses RLS on purpose.
-- Requires the users demo1@joinme.test ... demo4@joinme.test to exist already (see README.md).
-- Safe to run again: the previous demo activities are deleted first.

do $$
declare
    -- Dates follow the residence time zone, like the schema functions.
    v_today date := (now() at time zone 'Europe/Warsaw')::date;
    -- Next full hour, so the "today" activity is always still in the future when the seed runs.
    v_soon timestamp := date_trunc('hour', now() at time zone 'Europe/Warsaw') + interval '1 hour';
    v_demo1 uuid;
    v_demo2 uuid;
    v_demo3 uuid;
    v_demo4 uuid;
    v_id uuid;
begin
    select id into v_demo1 from auth.users where email = 'demo1@joinme.test';
    select id into v_demo2 from auth.users where email = 'demo2@joinme.test';
    select id into v_demo3 from auth.users where email = 'demo3@joinme.test';
    select id into v_demo4 from auth.users where email = 'demo4@joinme.test';

    if v_demo1 is null or v_demo2 is null or v_demo3 is null or v_demo4 is null then
        raise exception 'Demo users are missing. Create demo1@joinme.test ... demo4@joinme.test first.';
    end if;

    delete from public.activities where is_demo;

    -- 1. Soon, one free spot ("Table Tennis 1 of 2").
    insert into public.activities (type, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_demo)
    values ('TABLE_TENNIS', v_soon::date, v_soon::time, 60, 'GAMES_ROOM', 2,
            'Friendly match, any level. I bring the balls.', v_demo1, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id) values (v_id, v_demo1);

    -- 2. Outdoor activity (shows the weather panel), tomorrow.
    insert into public.activities (type, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_demo)
    values ('FOOTBALL', v_today + 1, '17:00', 90, 'SPORTS_COURT', 10,
            'Five against five if we are enough. Bring water.', v_demo2, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id) values (v_id, v_demo2), (v_id, v_demo3);

    -- 3. Full activity (4 of 4), tomorrow.
    insert into public.activities (type, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_demo)
    values ('BOARD_GAMES', v_today + 1, '19:00', 120, 'COMMON_ROOM', 4,
            'Catan and some card games.', v_demo4, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id)
    values (v_id, v_demo4), (v_id, v_demo1), (v_id, v_demo2), (v_id, v_demo3);

    -- 4. Open activity with several people, the day after tomorrow.
    insert into public.activities (type, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_demo)
    values ('MOVIE_NIGHT', v_today + 2, '20:00', 150, 'CINEMA_ROOM', 8,
            'We vote the movie at the door. Snacks welcome.', v_demo3, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id) values (v_id, v_demo3), (v_id, v_demo1), (v_id, v_demo4);

    -- 5. Already finished (yesterday).
    insert into public.activities (type, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_demo)
    values ('DARTS', v_today - 1, '18:00', 60, 'GAMES_ROOM', 4,
            'Quick tournament.', v_demo2, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id) values (v_id, v_demo2), (v_id, v_demo4);

    -- 6. Cancelled, with a custom name (type OTHER).
    insert into public.activities (type, custom_name, date, start_time, duration_minutes, location, max_participants, description, creator_id, is_cancelled, is_demo)
    values ('OTHER', 'Garden barbecue', v_today + 2, '16:00', 180, 'GARDEN', 12,
            'Cancelled because of the forecast.', v_demo1, true, true)
    returning id into v_id;
    insert into public.participants (activity_id, user_id) values (v_id, v_demo1), (v_id, v_demo3);
end;
$$;
