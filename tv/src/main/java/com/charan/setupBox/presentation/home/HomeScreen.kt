package com.charan.setupBox.presentation.home
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.charan.setupBox.BuildConfig
import com.charan.setupBox.presentation.home.components.AvatarImage
import com.charan.setupBox.presentation.home.components.HomeScreenTopBar
import com.charan.setupBox.presentation.home.components.ListRow
import com.charan.setupBox.presentation.home.components.TitleText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel= hiltViewModel(),
    navigateToLoginScreen : () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    BackHandler(state.showModelSheet) {
        viewModel.onEvent(HomeEvent.OnToggleModalSheet)

    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when(it){
                HomeEffect.NavigateToLoginScreen -> {
                    navigateToLoginScreen()

                }
                null -> {}
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            horizontalAlignment = Alignment.Start,
            userScrollEnabled = true
        ) {
            item {
                HomeScreenTopBar(
                    onRefresh = { viewModel.onEvent(HomeEvent.OnRefreshClick) },
                    onModalSheetOpen = { viewModel.onEvent(HomeEvent.OnToggleModalSheet) },
                    profilePicURL = state.profileURL
                )
            }
            items(state.categories.size) {
                val category = state.categories[it]
                TitleText(title = category.categoryName)
                ListRow(
                    items = category.channels,
                    onClick = { viewModel.onEvent(HomeEvent.OnChannelClick(it.channelURL,it.channelAppPackage)) },
                    shouldRequestFocus = it == 0

                )
            }
        }

        if (state.showModelSheet) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable {

                }
            )

            AnimatedVisibility(
                visible = state.showModelSheet,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                ModalDrawerContent(
                    email = state.email,
                    userName = state.userName,
                    profilePicURL = state.profileURL,
                    focusRequester = focusRequester,
                    onLogout = {
                        viewModel.onEvent(HomeEvent.OnLogoutClick)
                    },
                    isLogingOut = state.isLoading
                )
            }
            LaunchedEffect(state.showModelSheet) {
                if (state.showModelSheet) focusRequester.requestFocus()
            }
        }
    }



}

@Composable
fun ModalDrawerContent(
    email : String,
    userName : String,
    profilePicURL : String,
    focusRequester: FocusRequester,
    onLogout: () -> Unit,
    isLogingOut: Boolean
) {
    Box(
        modifier = Modifier.width(300.dp).fillMaxHeight().padding(5.dp).clip(RoundedCornerShape(16.dp))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(15.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarImage(imageUrl = profilePicURL)
                    Text(
                        text = userName.ifEmpty { email },
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                ListItem(
                    enabled = !isLogingOut,
                    selected = true,
                    onClick = onLogout,
                    modifier = Modifier.focusRequester(focusRequester),
                    headlineContent = { Text(text = "Logout") },
                    leadingContent = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Logout") },
                    trailingContent = {
                        AnimatedVisibility(visible = isLogingOut) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeCap = StrokeCap.Round)
                        }
                    }
                )
            }
        }

        Text(
            text = "Setup Box ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}
