package com.charan.setupBox.presentation.login

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.charan.setupBox.R
import com.charan.setupBox.presentation.login.components.CodeLabel
import com.charan.setupBox.presentation.navigation.OTPScreenNav
import com.charan.setupBox.utils.LoginState

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var code by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val loginState = uiState.loginState

    LaunchedEffect(Unit) {
        viewModel.onIntent(LoginViewModel.Intent.GetAuthenticationCode)
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.CodeGenerated -> {
                code = loginState.code
                viewModel.onIntent(LoginViewModel.Intent.ObserveOtpStatus(code!!))
            }
            is LoginState.CodeGeneratedError -> {
                Toast.makeText(context, loginState.error, Toast.LENGTH_LONG).show()
            }
            is LoginState.OTPError -> {
                Toast.makeText(context, loginState.error, Toast.LENGTH_LONG).show()
            }
            is LoginState.OTPSentTo -> {
                Toast.makeText(context, "OTP Sent to ${loginState.email}", Toast.LENGTH_LONG).show()
                navHostController.navigate(OTPScreenNav(loginState.email, code))
                viewModel.onIntent(LoginViewModel.Intent.ConsumeLoginState)
            }
            else -> Unit
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
        CodeLabel(code = code, modifier = Modifier, isGenerating = loginState is LoginState.Loading)
        Text(
            text = "3. Then, check your email for the OTP and enter it in the next screen",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_TYPE_TELEVISION,
    showBackground = true,
    showSystemUi = true,
    device = "id:tv_4k"
)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navHostController = rememberNavController())
}
