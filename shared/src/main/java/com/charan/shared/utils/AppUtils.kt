package com.charan.shared.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import kotlin.random.Random

object AppUtils {

    fun openLink(context: Context,appPackage: String,link:String){
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.setPackage(appPackage)
            ContextCompat.startActivity(context, intent, null)
        } catch (e:Exception){
            openApp(context,appPackage)
        }
    }

    private fun openApp(context: Context, packageName: String) {
        try {
            val packageManager: PackageManager = context.packageManager

            val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageName)

            if (launchIntent != null) {

                context.startActivity(launchIntent)
            } else {
                val playStoreIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                if (playStoreIntent.resolveActivity(packageManager) != null) {
                    context.startActivity(playStoreIntent)
                }
            }
        } catch (e: Exception){
            Toast.makeText(context, e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun generateRandomString(length: Int = 5): String {
        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun getColorForPlaceHolderBackground(): Color {
        val random = Random(System.currentTimeMillis())
        return Color(
            red = 204,
            green = 202,
            blue = 176
        )
    }

    fun getTextColorForPlaceholder() : Color {
        return Color(
            red = 51,
            green = 54,
            blue = 61
        )
    }


}