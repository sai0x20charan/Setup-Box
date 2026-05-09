package com.charan.setupBox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.charan.setupBox.presentation.navigation.NavAppHost
import com.charan.setupBox.ui.theme.SetupBoxTheme
import com.charan.shared.data.repository.SupabaseRepo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    @Inject lateinit var supabaseRepo: SupabaseRepo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keepSplash = true
        var isLoggedIn by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        setContent {
            LaunchedEffect(Unit) {
                isLoggedIn = supabaseRepo.loadSession()
            }
            LaunchedEffect(Unit) {
                keepSplash = false
            }
            SetupBoxTheme(isInDarkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    val navHostController = rememberNavController()
                    NavAppHost(navHostController = navHostController, isLoggedIn = isLoggedIn)
                }
            }
        }
    }
}
