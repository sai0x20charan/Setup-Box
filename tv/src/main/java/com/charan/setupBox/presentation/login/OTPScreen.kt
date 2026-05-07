package com.charan.setupBox.presentation.login

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.charan.setupBox.R
import com.charan.setupBox.presentation.navigation.HomeScreenNav
import com.charan.setupBox.utils.LoginState
import com.charan.setupBox.utils.ProcessState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OTPScreen(
    navHostController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel(),
    emailId: String,
    code: String
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    val uiState by viewModel.uiState.collectAsState()
    val authenticationStatus = uiState.authenticationStatus
    val loginState = uiState.loginState
    val otpTextField = uiState.otpTextField

    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.OTPVerificationError -> {
                Toast.makeText(context, loginState.error, Toast.LENGTH_LONG).show()
                viewModel.onIntent(LoginViewModel.Intent.ConsumeLoginState)
            }
            LoginState.OTPVerified -> {
                viewModel.onIntent(LoginViewModel.Intent.CheckAuthenticationStatus)
                viewModel.onIntent(LoginViewModel.Intent.ConsumeLoginState)
            }
            else -> Unit
        }
    }

    LaunchedEffect(authenticationStatus) {
        when (authenticationStatus) {
            is ProcessState.Error -> {
                Toast.makeText(context, authenticationStatus.error, Toast.LENGTH_LONG).show()
                viewModel.onIntent(LoginViewModel.Intent.ConsumeAuthenticationStatus)
            }
            is ProcessState.Success -> {
                viewModel.onIntent(LoginViewModel.Intent.ChangeAuthenticationStatus(code))
                navHostController.navigate(HomeScreenNav) {
                    popUpTo(navHostController.graph.id) { inclusive = true }
                }
                viewModel.onIntent(LoginViewModel.Intent.ConsumeAuthenticationStatus)
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(20.dp).imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Icon",
            modifier = Modifier.size(120.dp).padding(bottom = 30.dp)
        )

        Row(modifier = Modifier.padding(bottom = 20.dp)) {
            Text("Enter OTP sent to", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(end = 5.dp), fontWeight = FontWeight.Light)
            Text(emailId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        BasicTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = otpTextField,
            onValueChange = { viewModel.onIntent(LoginViewModel.Intent.UpdateOtpText(it)) },
            modifier = Modifier
                .border(2.dp, if (isFocused) Color.White else Color.Gray, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .padding(20.dp)
                .width(300.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { viewModel.onIntent(LoginViewModel.Intent.VerifyOtpStatus(emailId)) },
            enabled = loginState !is LoginState.Loading,
            modifier = Modifier.width(100.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.then(
                        if (loginState is LoginState.Loading) Modifier.animateContentSize().padding(end = 5.dp) else Modifier
                    )
                )

                AnimatedVisibility(visible = loginState is LoginState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeCap = StrokeCap.Round)
                }
            }
        }
    }
}
