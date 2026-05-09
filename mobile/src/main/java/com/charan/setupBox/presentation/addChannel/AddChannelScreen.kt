package com.charan.setupBox.presentation.addChannel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.charan.setupBox.presentation.addChannel.components.CustomTextField
import com.charan.setupBox.presentation.addChannel.components.CustomTextForPackages
import com.charan.setupBox.presentation.addChannel.components.PreviewAlertBox
import com.charan.setupBox.presentation.addChannel.components.PreviewButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelScreen(
    viewModel: AddChannelViewModel= hiltViewModel(),
    navigateBack : () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val scroll = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddChannelEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is AddChannelEffect.NavigateBack -> {
                   navigateBack()
                }
                else -> {}
            }
        }
    }

    if(state.showPreviewBox){
        PreviewAlertBox(
            imageLink = state.channelData.channelPhoto,
            onClick = {
                viewModel.onEvent(AddChannelEvent.OnTogglePreviewBox)
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text( if(state.isEdit) "Edit Channel" else "Add Channel") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(AddChannelEvent.OnNavigateBack)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (state.isEdit) {
                        IconButton(onClick = {
                            viewModel.onEvent(AddChannelEvent.OnDelete)

                        }) {
                            if (state.isSaving) CircularProgressIndicator()
                            Icon(Icons.Filled.Delete, null)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddChannelEvent.OnSave)
            }) {
                if (state.isSaving) {
                    CircularProgressIndicator(strokeCap = StrokeCap.Round, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                }
            }
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(it)) {
            item {
                CustomTextField(
                    value = state.channelData.channelName,
                    onValueChange = { viewModel.onEvent(AddChannelEvent.OnChannelNameChange(it)) },
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    label = "Channel Name"
                )

                CustomTextField(
                    value = state.channelData.channelLink,
                    onValueChange = { viewModel.onEvent(AddChannelEvent.OnChannelLinkChange(it)) },
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    label = "Channel Link"
                )

                CustomTextField(
                    value = state.channelData.channelPhoto,
                    onValueChange = { viewModel.onEvent(AddChannelEvent.OnChannelPhotoChange(it)) },
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    label = "Channel Photo Link",
                    trailingIcon = {
                        PreviewButton(
                            onClick = {
                                viewModel.onEvent(AddChannelEvent.OnTogglePreviewBox)
                            },
                            isButtonEnabled = true
                        )
                    }
                )

                CustomTextForPackages(
                    value = state.channelData.appPackage,
                    onValueChange = { viewModel.onEvent(AddChannelEvent.OnPackageChange(it)) },
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    label = "Package Name",
                    packages = state.distinctAppPackages
                )

                ListItem(
                    headlineContent = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text(text = "Select Category", modifier = Modifier.padding(top = 10.dp))
                            Spacer(Modifier.weight(1f))

                            TextButton(
                                onClick = {
                                    viewModel.onEvent(AddChannelEvent.OnToggleCategoryDropDown)
                                },
                                contentPadding = PaddingValues(3.dp)
                            ) {
                                Text(state.channelData.category)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
                                    DropdownMenu(
                                        expanded = state.showCategoryDropDown,
                                        onDismissRequest = { viewModel.onEvent(AddChannelEvent.OnToggleCategoryDropDown) },
                                    ) {
                                        state.categories.forEach {
                                            DropdownMenuItem(
                                                text = { Text(it, textAlign = TextAlign.Center) },
                                                onClick = {
                                                    viewModel.onEvent(AddChannelEvent.OnCategoryChange(it))
                                                    viewModel.onEvent(AddChannelEvent.OnToggleCategoryDropDown)

                                                },
                                                modifier = if (state.channelData.category == it) {
                                                    Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                                                } else Modifier
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 25.dp).clickable {
                            viewModel.onEvent(AddChannelEvent.OnToggleCategoryDropDown)

                    }
                )
            }
        }
    }
}
