package com.charan.setupBox.presentation.settings.settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.setupBox.presentation.common.components.CustomAppBar
import com.charan.setupBox.presentation.common.components.CustomSegmentedLists
import com.charan.setupBox.presentation.navigation.AboutAppNav
import com.charan.setupBox.presentation.navigation.AccountScreenNav
import com.charan.setupBox.presentation.settings.components.SettingHeader
import com.charan.setupBox.ui.theme.IndexItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel ,
    navigateToAccountScreen : () -> Unit = {},
    navigateToAboutAppScreen : () -> Unit = {},
    navigateBack : () -> Unit = {}
) {

    val scroll = TopAppBarDefaults.pinnedScrollBehavior()
    val uriHandler = LocalUriHandler.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is SettingsEffect.NavigateToAccountScreen -> {
                    navigateToAccountScreen()
                }
                is SettingsEffect.NavigateToAboutAppScreen -> {
                    navigateToAboutAppScreen()
                }

                SettingsEffect.NavigateBack -> {
                    navigateBack()
                }

                is SettingsEffect.OpenLink -> {
                    uriHandler.openUri(effect.url)

                }
                else -> {}
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            CustomAppBar(
                title = "Settings",
                showBackButton = true,
                onBackButtonClick = {
                    viewModel.onEvent(SettingsEvent.OnNavigateBack)
                },
                scrollBehavior = scroll

            )

        }
    ) { padding->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(horizontal = 16.dp)
            .fillMaxSize()) {

            item {
                SettingHeader(
                    title = "Account"
                )
                CustomSegmentedLists(
                    headLineContent = {
                        Text(
                            text = "Account Settings"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Account",
                        )
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnAccountClick)
                    },
                    index = IndexItem.FIRST_AND_LAST

                )

            }

            item {
                SettingHeader(
                    title = "About"
                )
                CustomSegmentedLists(
                    headLineContent = {
                        Text("Open Source Licenses")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.WorkspacePremium,
                            contentDescription = "Open Source Licenses",
                        )
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnOpenSourceLicensesClick)

                    },
                    index = IndexItem.FIRST

                )

                CustomSegmentedLists(
                    headLineContent = {
                        Text(
                            text = "Source Code"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = "Source Code",
                        )
                    },
                    onClick = {
                        viewModel.onEvent(SettingsEvent.OnOpenGitHubClick)

                    },
                    index = IndexItem.MIDDLE

                )

                CustomSegmentedLists(
                    headLineContent = {
                        Text(
                            text = "App Version"
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Code,
                            contentDescription = "App Version",
                        )
                    },
                    trailingContent = {
                        Text(
                            "${state.appInfo.versionName} (${state.appInfo.versionCode})"
                        )
                    },
                    onClick = {

                    },
                    index = IndexItem.LAST
                )



            }

        }

    }
}
