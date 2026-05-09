package com.charan.setupBox.presentation.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomLargeAppBar(
    title : String,
    showBackButton : Boolean = false,
    onBackButtonClick : () -> Unit = {},
    actions : @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeFlexibleTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                BackButton { onBackButtonClick() }
            } else null
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomMediumAppBar(
    title : String,
    showBackButton : Boolean = false,
    onBackButtonClick : () -> Unit = {},
    actions : @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    MediumFlexibleTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                BackButton { onBackButtonClick() }
            } else null
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomAppBar(
    title : String,
    showBackButton : Boolean = false,
    onBackButtonClick : () -> Unit = {},
    actions : @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                BackButton { onBackButtonClick() }
            } else null
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )

}

@Composable
private fun BackButton(
    onClick :() -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.shapes(),
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}
