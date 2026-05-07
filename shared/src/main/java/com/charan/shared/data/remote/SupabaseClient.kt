package com.charan.shared.data.remote

import com.charan.shared.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

class SupabaseClient {
    val client= createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY

    ){
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Functions)
    }
}