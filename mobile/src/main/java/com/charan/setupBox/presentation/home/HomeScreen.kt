package com.charan.setupBox.presentation.home
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.setupBox.presentation.common.components.CustomAppBar
import com.charan.setupBox.presentation.common.components.CustomDropDown
import com.charan.setupBox.presentation.common.components.CustomSegmentedLists
import com.charan.setupBox.presentation.common.components.CustomTooltipBox
import com.charan.setupBox.presentation.common.components.ThumbnailImage
import com.charan.setupBox.presentation.common.model.DropDownItemData
import com.charan.setupBox.presentation.home.components.SyncStatusIndicators


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    homeViewModel : HomeViewModel = hiltViewModel(),
    navigateToSettings : () -> Unit = {},
    navigateToAddChannel : (id : Long?, channelLink : String?) -> Unit

) {

    val state by homeViewModel.homeState.collectAsStateWithLifecycle()
    val scroll = TopAppBarDefaults.pinnedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    val dropDownList = listOf(
        DropDownItemData(
            icon = Icons.Filled.Settings,
            text = "Settings",
            onClick = {
                homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                homeViewModel.onEvent(HomeEvent.OnSettingsClick)
            }
        ),
        DropDownItemData(
            icon = Icons.Filled.Refresh,
            text = "Refresh",
            onClick = {
                homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                homeViewModel.onEvent(HomeEvent.OnRefresh)
            }
        )
    )

    LaunchedEffect(Unit) {
        homeViewModel.homeEffect.collect { effect ->
            when(effect) {
                is HomeEffect.NavigateToSettingsScreen -> {
                    navigateToSettings()
                }
                is HomeEffect.NavigateToAddChannelScreen -> {
                    navigateToAddChannel(effect.id, "")
                }

                else -> {}
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            CustomAppBar(
                title = "SetupBox",
                scrollBehavior = scroll,
                actions = {
                    SyncStatusIndicators(
                        isSyncing = state.syncState.isSyncing,
                        hasError = state.syncState.hasError,
                        errorMessage = state.syncState.errorMessage,
                        onSyncClick = {
                            homeViewModel.onEvent(HomeEvent.OnSyncClick)
                        }

                    )

                    IconButton(onClick = {
                        homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                    },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    CustomDropDown(
                        isExpanded = state.showDropDown,
                        items = dropDownList,
                        onDismiss = {
                            homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                        },
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                homeViewModel.onEvent(HomeEvent.OnChannelClick())
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        PullToRefreshBox(
            isRefreshing = state.isFetchingData,
            state = pullToRefreshState,
            onRefresh = {
                homeViewModel.onEvent(HomeEvent.OnRefresh)
            },
            modifier = Modifier.fillMaxSize().padding(it),
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = state.isFetchingData,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Box {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                    items(state.allChannelData.size) { item ->
                        val channelData = state.allChannelData[item]
                        CustomSegmentedLists(
                            headLineContent = {
                                Text(
                                    channelData.channelName,

                                    )
                            },
                            leadingContent = {
                                ThumbnailImage(
                                    imageUrl = channelData.channelPhoto,
                                    width = 80.dp,
                                    height = 50.dp,
                                    fallBackText = channelData.channelName
                                )

                            },
                            trailingContent = {
                                if(!(channelData.isSynced)){
                                    CustomTooltipBox(
                                        tooltipText = "Channel is not synced.",
                                        content = {
                                            Icon(
                                                imageVector = Icons.Filled.CloudOff,
                                                contentDescription = "Not Synced",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }

                                    )

                                }
                            },

                            index = item,
                            count = state.allChannelData.size,
                            onClick = {
                                homeViewModel.onEvent(HomeEvent.OnChannelClick(channelData.id))
                            },
                        )
                    }
                }
            }
        }
    }
}
