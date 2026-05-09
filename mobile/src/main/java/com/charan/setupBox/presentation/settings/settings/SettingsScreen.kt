package com.charan.setupBox.presentation.settings.settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.charan.setupBox.presentation.navigation.AboutAppNav
import com.charan.setupBox.presentation.navigation.AccountScreenNav
import com.charan.setupBox.presentation.settings.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel ,
    navigateToAccountScreen : () -> Unit = {},
    navigateToAboutAppScreen : () -> Unit = {},
    navigateBack : () -> Unit = {}
) {

    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                else -> {}
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(SettingsEvent.OnBackClick)
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "back")

                    }
                }


            )

        }
    ) { padding->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            item {
                SettingsItem(icon = Icons.Outlined.AccountCircle, title = "Account"){
                    viewModel.onEvent(SettingsEvent.OnAccountClick)


                }
                HorizontalDivider()
                SettingsItem(icon = Icons.Outlined.Info, title = "About App"){
                    viewModel.onEvent(SettingsEvent.OnAboutAppClick)

                }
            }
        }

    }
}
