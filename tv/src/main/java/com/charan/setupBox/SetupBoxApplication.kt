package com.charan.setupBox

import android.app.Application
import com.charan.shared.supabase.SupabaseProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SetupBoxApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseProvider.initialize(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
    }
}
