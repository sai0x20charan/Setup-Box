package com.charan.setupBox.presentation.settings.account

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.charan.setupBox.presentation.navigation.HomeScreenNav
import com.charan.setupBox.presentation.navigation.LoginScreenNav
import com.charan.setupBox.presentation.settings.components.AvatarImage
import com.charan.setupBox.presentation.settings.settings.SettingsEvent
import com.charan.setupBox.utils.ProcessState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(

    viewModel: com.charan.setupBox.presentation.settings.settings.SettingsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current


    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Account") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "back")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(scroll.nestedScrollConnection)
        ) {
            item {
//                ListItem(
//                    leadingContent = { AvatarImage(imageUrl = UserSessionManager.getProfilePic()) },
//                    headlineContent = { Text(text = UserSessionManager.getEmail() ?: "null") }
//                )
                ListItem(
                    headlineContent = { Text(text = "Logout") },
                    modifier = Modifier.padding(start = 20.dp).clickable {
                        viewModel.onEvent(SettingsEvent.OnLogoutClick)
                    },
                    trailingContent = {
                        AnimatedVisibility(visible = state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp).fillMaxWidth(),
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text(text = "Authenticate TV") },
                    modifier = Modifier.padding(start = 20.dp).clickable {
                        viewModel.onEvent(SettingsEvent.OnToggleAuthenticationSheet)
                    }
                )
            }
        }
    }

    if (state.showEnterCodeDialog) {
        ModalBottomSheet(onDismissRequest = { viewModel.onEvent(SettingsEvent.OnToggleAuthenticationSheet) }) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Enter Code", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                OutlinedTextField(
                    value = state.code,
                    onValueChange = { viewModel.onEvent(SettingsEvent.OnAuthenticateCodeChange(it)) },
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
                Button(
                    onClick = { viewModel.onEvent(SettingsEvent.OnAuthenticateClick) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    Text(
                        text = "Authenticate",
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
