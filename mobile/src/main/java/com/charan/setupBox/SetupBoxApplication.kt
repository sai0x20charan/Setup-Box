package com.charan.setupBox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SetupBoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
