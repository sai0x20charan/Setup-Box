package com.charan.setupBox.presentation.login.otp

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.charan.setupBox.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OTPScreen(
    viewModel: OTPViewModel = hiltViewModel(),
    navigateToHomeScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    val state by viewModel.state.collectAsState()

    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.effect.collectLatest {
            when (it) {
                OTPEffect.NavigateToHomeScreen -> {
                    navigateToHomeScreen()
                }

                is OTPEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }

                null -> {}
            }
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
            Text(state.email, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        BasicTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = state.otpCode,
            onValueChange = { viewModel.onEvent(OTPEvent.OnOTPCodeChange(it)) },
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
            onClick = { viewModel.onEvent(OTPEvent.OnOTPCodeVerifyClick) },
            enabled = !state.isLoading,
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
                        if (state.isLoading) Modifier.animateContentSize().padding(end = 5.dp) else Modifier
                    )
                )

                AnimatedVisibility(visible = state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeCap = StrokeCap.Round)
                }
            }
        }
    }
}
