# JoinMe database (Supabase)

## Files

- `schema.sql`: tables (`activities`, `participants`), indexes, RLS policies, grants and the
  functions the app calls (`join_activity`, `leave_activity`, `create_activity`,
  `cancel_activity`, `delete_my_account`). Users cannot write to the tables directly.
- `seed.sql`: fictional demo activities (`is_demo = true`) created by the demo users.

## Order

1. Run `schema.sql` (it can be run again without breaking anything).
2. Create the four demo users in Supabase Auth: `demo1@joinme.test` ... `demo4@joinme.test`
   (email + password, no email confirmation). From the dashboard use Authentication > Users > Add user,
   or call the Auth admin API `POST /auth/v1/admin/users` with the service role key
   (`{"email": "demo1@joinme.test", "password": "...", "email_confirm": true}`).
3. Run `seed.sql`.

Run the SQL files as the `postgres` role (SQL editor or Management API).

In Authentication settings turn off "Confirm email" and set the minimum password length to 8.

`rls_auto_enable` is a function Supabase creates itself when "Enable RLS automatically" is on. `schema.sql` revokes its execute permission from the API roles, since only an event trigger needs it. If the function does not exist in your project, that step is skipped.

## Reloading the seed

`seed.sql` first deletes all rows with `is_demo = true` (their participants are removed by cascade)
and inserts them again with dates relative to today, so run it again before every demo, because the
dates go stale. Real user data is not touched.
