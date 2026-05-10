package com.charan.setupBox.presentation.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomAlertDialog(
    titleText : String,
    descriptionText : String,
    confirmButtonText : String = "OK",
    dismissButtonText : String? = null,
    onConfirm : () -> Unit = { },
    onDismiss : () -> Unit = { },
    icon : ImageVector? = null
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = titleText) },
        text = { Text(text = descriptionText) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = if (dismissButtonText != null) {
            {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = dismissButtonText)
                }
            }
        } else null,
        icon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        } else null
    )
}