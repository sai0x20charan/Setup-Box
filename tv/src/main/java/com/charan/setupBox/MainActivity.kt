package com.charan.setupBox

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.charan.setupBox.presentation.navigation.NavAppHost
import com.charan.setupBox.ui.theme.SetupBoxTheme
import com.charan.shared.supabase.SupabaseUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLoggedIn = mutableStateOf(false)
        val isExecuted = mutableStateOf(false)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !isExecuted.value
            }
            lifecycleScope.launch {
                isLoggedIn.value = SupabaseUtils.getSessionToken(this@MainActivity)
                Log.d("TAG", "onCreate: $isLoggedIn")
                delay(1000)
                isExecuted.value = true
            }
        }
        setContent {
            SetupBoxTheme(isInDarkTheme = true) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,

                ) {
                    val navHostController = rememberNavController()
                    NavAppHost(navHostController = navHostController,isLoggedIn.value)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as Application


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SetupBoxTheme {
        Greeting("Android")
    }
}

fun openYoutubeLink(youtubeID: String,context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@ThePermitRoomMedia"))
    intent.setPackage("com.google.android.youtube.tv")
    startActivity(context,intent,null)

//    val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "https://www.youtube.com/@ThePermitRoomMedia"))
//    val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@ThePermitRoomMedia"))
//    try {
//        context.startActivity(intentApp)
//    } catch (ex: ActivityNotFoundException) {
//        context.startActivity(intentBrowser)
//    }

}