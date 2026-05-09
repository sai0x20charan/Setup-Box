package com.charan.shared.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


class AppLauncher @Inject constructor(
    private val context : Context
) {
    fun openLink(appPackage: String, link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                setPackage(appPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openApp( appPackage)
        }
    }

    fun openApp(appPackage: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(appPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent ?: return)
    }
}