package com.charan.setupBox.presentation.addChannel.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldForPackages(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    packages: List<String?>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = MenuDefaults.groupShape(0, 1).shape
        ) {
            packages.fastForEachIndexed { index, name ->
                val itemShape = MenuDefaults.itemShape(index, packages.size)

                DropdownMenuItem(
                    text = { Text(name.orEmpty()) },
                    onCheckedChange = { onValueChange(name.orEmpty()) ; expanded = false },
                    shapes = itemShape,
                    checked = false,
                    colors = MenuDefaults.selectableItemColors()
                        .copy( containerColor = MaterialTheme.colorScheme.surfaceContainer )
                )
            }
        }
    }
}