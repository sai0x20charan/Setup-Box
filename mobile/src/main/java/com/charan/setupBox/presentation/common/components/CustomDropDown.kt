package com.charan.setupBox.presentation.common.components
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.charan.setupBox.presentation.common.model.DropDownItemData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomDropDown(
    items: List<DropDownItemData>,
    isExpanded : Boolean = false,
    onDismiss : () -> Unit = { },
    containerColor : Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    DropdownMenuPopup(
        expanded = isExpanded,
        onDismissRequest = {
            onDismiss()
        },

        ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(0, 1),
            containerColor = containerColor

        ) {
            items.fastForEachIndexed { index, item ->

                val itemShape = MenuDefaults.itemShape(index, items.size)

                DropdownMenuItem(
                    modifier = Modifier,
                    text = {

                        Text(text = item.text)
                    },
                    leadingIcon = {
                        if (item.icon != null) {

                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.text
                            )


                        }
                    },
                    shapes = itemShape,
                    onCheckedChange = {
                        item.onClick()
                    },
                    checked = false,
                    colors = MenuDefaults.selectableItemColors().copy(
                        containerColor = containerColor
                    )
                )
            }
        }

    }

}
