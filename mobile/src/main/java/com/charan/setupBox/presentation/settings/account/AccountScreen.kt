package com.charan.setupBox.presentation.settings.account

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.setupBox.presentation.common.components.CustomAlertDialog
import com.charan.setupBox.presentation.common.components.CustomAppBar
import com.charan.setupBox.presentation.common.components.CustomSegmentedLists
import com.charan.setupBox.presentation.settings.components.SettingHeader
import com.charan.setupBox.presentation.settings.components.UserInfoItem
import com.charan.setupBox.presentation.settings.settings.SettingsEffect
import com.charan.setupBox.presentation.settings.settings.SettingsEvent
import com.charan.setupBox.presentation.settings.settings.SettingsViewModel
import com.charan.setupBox.ui.theme.IndexItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToLoginScreen : () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> navigateToBack()
                is SettingsEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()

                }
                is SettingsEffect.NavigateToLoginScreen -> {
                    navigateToLoginScreen()

                }

                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = "Account",
                onBackButtonClick = { viewModel.onEvent(SettingsEvent.OnBackClick) },
                showBackButton = true,
                scrollBehavior = scroll
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .nestedScroll(scroll.nestedScrollConnection)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                UserInfoItem(
                    userName = state.userInfo.userName,
                    email = state.userInfo.email,
                    profilePic = state.userInfo.profilePic,
                )
                SettingHeader(
                    title =  "Actions",
                )

                CustomSegmentedLists(
                    headLineContent = {
                        Text("Authenticate TV")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Tv,
                            contentDescription = "Authenticate TV",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnToggleAuthenticationSheet)

                    },
                    index = IndexItem.FIRST


                )
                CustomSegmentedLists(
                    headLineContent = {
                        Text("Logout")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnToggleLogoutDialog)
                    },
                    index =  IndexItem.LAST
                )
            }
        }
    }
    if (state.showEnterCodeDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(SettingsEvent.OnToggleAuthenticationSheet) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Authenticate TV",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enter the code displayed on your TV screen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = state.code,
                    onValueChange = { viewModel.onEvent(SettingsEvent.OnAuthenticateCodeChange(it)) },
                    label = { Text("TV Code") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onEvent(SettingsEvent.OnAuthenticateClick) },
                    enabled = !state.isAuthenticating,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Authenticate")
                    if (state.isAuthenticating) {
                        LoadingIndicator(modifier = Modifier.size(ButtonDefaults.IconSize))
                    }
                }
            }
        }
    }
    if(state.showLogoutDialog){
        CustomAlertDialog(
            titleText = "Logout",
            descriptionText = "Are you sure you want to logout?",
            confirmButtonText = "Logout",
            onConfirm = { viewModel.onEvent(SettingsEvent.OnLogoutClick) },
            onDismiss = { viewModel.onEvent(SettingsEvent.OnToggleLogoutDialog) },
            dismissButtonText = "Cancel",
            icon = Icons.AutoMirrored.Outlined.Logout,
        )
    }
}
