package com.joinme.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Duration.Companion.seconds

/**
 * Creates the one Supabase client of the app.
 * Auth saves the session on the device and loads it again on the next start.
 */
fun createSupabase(url: String, key: String): SupabaseClient = createSupabaseClient(url, key) {
    // Short timeout so "no connection" is detected quickly.
    requestTimeout = 10.seconds
    install(Auth)
    install(Postgrest)
}
