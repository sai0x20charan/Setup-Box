package com.charan.setupBox.presentation.addChannel
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.charan.setupBox.presentation.addChannel.components.CustomTextField
import com.charan.setupBox.presentation.addChannel.components.CustomTextFieldForPackages
import com.charan.setupBox.presentation.addChannel.components.PreviewAlertBox
import com.charan.setupBox.presentation.addChannel.components.PreviewButton
import com.charan.setupBox.presentation.addChannel.components.SelectCategoryField
import com.charan.setupBox.presentation.common.components.CustomAlertDialog
import com.charan.setupBox.presentation.common.components.CustomAppBar
import com.charan.setupBox.presentation.common.model.DropDownItemData

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

    val categoriesDropDownItems = remember(state.categories,state.channelData.category) {
        state.categories.map { category ->
            DropDownItemData(
                text = category,
                onClick = {
                    viewModel.onEvent(AddChannelEvent.OnCategoryChange(category))
                },
                isSelected = state.channelData.category == category
            )
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

    if (state.showDeleteConfirmation) {
        CustomAlertDialog(
            titleText = "Delete Channel",
            onDismiss = {
                viewModel.onEvent(AddChannelEvent.OnToggleDeleteConfirmation)
            },
            onConfirm = {
                viewModel.onEvent(AddChannelEvent.OnToggleDeleteConfirmation)
                viewModel.onEvent(AddChannelEvent.OnDelete)
            },
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            descriptionText = "Are you sure you want to delete this channel?"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            CustomAppBar(
                title = if(state.isEdit) "Edit Channel" else "Add Channel",
                showBackButton = true,
                onBackButtonClick = {
                    viewModel.onEvent(AddChannelEvent.OnNavigateBack)
                },
                actions = {
                    if (state.isEdit) {
                        FilledTonalIconButton(onClick = {
                            viewModel.onEvent(AddChannelEvent.OnToggleDeleteConfirmation)
                        },
                            shapes = IconButtonDefaults.shapes(),
                            modifier = Modifier,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                contentColor = MaterialTheme.colorScheme.onErrorContainer


                            )
                        ) {
                            Icon(Icons.Filled.Delete, null)
                        }
                    }
                },
                scrollBehavior = scroll
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

                CustomTextFieldForPackages(
                    value = state.channelData.appPackage,
                    onValueChange = { viewModel.onEvent(AddChannelEvent.OnPackageChange(it)) },
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    label = "Package Name",
                    packages = state.distinctAppPackages
                )
                SelectCategoryField(
                    categories = categoriesDropDownItems,
                    selectedCategory = state.channelData.category,
                    modifier = Modifier.padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    onDropDownExpandClick = {
                        viewModel.onEvent(AddChannelEvent.OnToggleCategoryDropDown)
                    },
                    isDropDownExpanded = state.showCategoryDropDown
                )
            }
        }
    }
}
