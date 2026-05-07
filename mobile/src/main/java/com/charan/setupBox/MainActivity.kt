package com.charan.setupBox

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.charan.setupBox.presentation.navigation.NavigationAppHost
import com.charan.setupBox.ui.theme.SetupBoxTheme
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.data.repository.SyncManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var sharedUrl by mutableStateOf<String?>(null)
    private var isLoggedIn by mutableStateOf(true)

    private var keepScreen by mutableStateOf(true)
    @Inject lateinit var supabaseRepo : SupabaseRepo

    @Inject lateinit var syncManager: SyncManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            syncManager.syncListener()
        }
        sharedUrl = intent.getSharedURL().takeIf { it.isNotBlank() }
        installSplashScreen().setKeepOnScreenCondition { keepScreen }
        setContent {
            LaunchedEffect(Unit) {
                isLoggedIn = supabaseRepo.loadSession()
                keepScreen = false

            }
            SetupBoxTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navHostController = rememberNavController()
                    NavigationAppHost(
                        navHostController = navHostController,
                        sharedURL = sharedUrl,
                        isLoggedIn = isLoggedIn,
                    )
                }
            }
        }
    }
}

fun Intent.getSharedURL(): String {
    if (Intent.ACTION_SEND == action && type != null) {
        return getStringExtra(Intent.EXTRA_TEXT).orEmpty()
    }
    if (Intent.ACTION_VIEW == action && data != null) {
        return dataString.orEmpty()
    }
    return ""
}
