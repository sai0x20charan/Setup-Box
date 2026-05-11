package com.charan.setupBox.presentation.addChannel.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.setupBox.presentation.common.components.CustomDropDown
import com.charan.setupBox.presentation.common.model.DropDownItemData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectCategoryField(
    selectedCategory : String,
    onDropDownExpandClick : () -> Unit,
    categories : List<DropDownItemData>,
    isDropDownExpanded : Boolean = false,
    modifier: Modifier


) {

    ListItem(
        modifier = modifier,
        content = {
            Text(text = "Select Category")
        },
        trailingContent = {
            TextButton(
                onClick = { onDropDownExpandClick() },
                shapes = ButtonDefaults.shapes(),
                contentPadding = PaddingValues(4.dp),
            ) {
                Text(text = selectedCategory)
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null
                )
            }
            CustomDropDown(
                items = categories,
                isExpanded = isDropDownExpanded,
                onDismiss = { onDropDownExpandClick() },

            )
        },
        contentPadding = PaddingValues(start = 4.dp),
        onClick = {
                onDropDownExpandClick()
        }

    )

}