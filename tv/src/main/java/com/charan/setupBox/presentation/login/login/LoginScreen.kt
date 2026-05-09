package com.charan.setupBox.presentation.login.login
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.charan.setupBox.R
import com.charan.setupBox.presentation.login.components.CodeLabel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel= hiltViewModel(),
    navigateToOTPScreen : (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when(it){
                is LoginEffect.NavigateToOTPScreen -> {
                    navigateToOTPScreen(it.email)

                }
                is LoginEffect.ShowToast -> {
                    Toast.makeText(context,it.message, Toast.LENGTH_LONG).show()
                }
                null -> {

                }

                else -> {}
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.app_icon), contentDescription = "appIcon", modifier = Modifier.size(100.dp))
        Text(
            text = "1. Open  \"Setup Box\" app on your mobile device.",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = "2. Enter the code shown below:",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 20.dp, top = 10.dp),
            textAlign = TextAlign.Start
        )
        CodeLabel(code = state.authenticationCode, modifier = Modifier, isGenerating = state.isLoading)
        Text(
            text = "3. Then, check your email for the OTP and enter it in the next screen",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp),
            textAlign = TextAlign.Start
        )
    }
}
