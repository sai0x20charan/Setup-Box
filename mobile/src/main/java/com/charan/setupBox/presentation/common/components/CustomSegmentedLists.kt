package com.charan.setupBox.presentation.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.setupBox.ui.theme.IndexItem
import com.charan.setupBox.ui.theme.customListItemShapes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomSegmentedLists(
    headLineContent: @Composable () -> Unit,
    supportingContent : @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    leadingContent : @Composable (() -> Unit)? = null,
    index : IndexItem,
    onClick : () -> Unit = { }
) {
    SegmentedListItem(
     onClick = { onClick()},
        content = headLineContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shapes = customListItemShapes(index),
        modifier = Modifier.padding(1.dp),
        contentPadding = PaddingValues(16.dp)
    )
}