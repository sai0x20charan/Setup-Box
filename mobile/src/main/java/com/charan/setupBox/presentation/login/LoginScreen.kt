package com.charan.setupBox.presentation.login
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.setupBox.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel= hiltViewModel(),
    navigateToHomeScreen : () -> Unit = {}
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is LoginEffect.NavigateToHomeScreen -> {
                    navigateToHomeScreen()
                }
                else -> {}

            }
        }
    }
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = "Setup Box",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { viewModel.onEvent(LoginEvent.OnLoginWithGoogleClick) },
                modifier = Modifier.animateContentSize().fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                enabled = !state.isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.applogo),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Continue with Google",
                        modifier = Modifier.animateContentSize().then(
                            if (state.isLoading) Modifier.padding(end = 10.dp) else Modifier
                        )
                    )
                    AnimatedVisibility(visible = state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp).fillMaxWidth(),
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }
}
