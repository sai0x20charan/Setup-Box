package com.charan.setupBox.presentation.home
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.setupBox.presentation.common.components.ThumbnailImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel : HomeViewModel = hiltViewModel(),
    navigateToSettings : () -> Unit = {},
    navigateToAddChannel : (id : Long?, channelLink : String?) -> Unit

) {

    val state by homeViewModel.homeState.collectAsStateWithLifecycle()
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
            LargeTopAppBar(
                title = { Text("Setup Box") },
                scrollBehavior = scroll,
                actions = {
                    IconButton(onClick = {
                        homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                    }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = state.showDropDown,
                        onDismissRequest = {
                            homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                homeViewModel.onEvent(HomeEvent.ToggleShowDropDown)
                                homeViewModel.onEvent(HomeEvent.OnSettingsClick)

                            }
                        )
                    }
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
            isRefreshing = state.loading,
            onRefresh = {
                homeViewModel.onEvent(HomeEvent.OnRefresh)
            },
            modifier = Modifier.padding(it)
        ) {
            Box {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.allChannelData.size) { item ->
                        val channelData = state.allChannelData[item]
                        ListItem(
                            headlineContent = {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(top = 20.dp, bottom = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ThumbnailImage(
                                        imageUrl = channelData.channelPhoto,
                                        width = 80,
                                        height = 50,
                                        fallBackText = channelData.channelName
                                    )
                                    Text(
                                        channelData.channelName,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                homeViewModel.onEvent(HomeEvent.OnChannelClick(channelData.id ?: -1))
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
