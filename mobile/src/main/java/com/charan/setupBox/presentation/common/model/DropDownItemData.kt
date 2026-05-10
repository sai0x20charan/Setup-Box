package com.charan.setupBox.presentation.common.model

import androidx.compose.ui.graphics.vector.ImageVector

data class DropDownItemData(
    val icon: ImageVector? = null,
    val text: String,
    val onClick: () -> Unit,
    val isSelected : Boolean = false
)
