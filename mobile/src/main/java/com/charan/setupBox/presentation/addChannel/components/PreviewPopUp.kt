package com.charan.setupBox.presentation.addChannel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charan.setupBox.presentation.common.components.ThumbnailImage

@Composable
fun PreviewAlertBox(
    imageLink:String,
    onClick:(Boolean)->Unit
) {
    AlertDialog(
        onDismissRequest = {
            onClick(false)
        },
        title = { Text("Preview") },
        text = {
                Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center) {
                    ThumbnailImage(
                        imageUrl = imageLink,
                        modifier = Modifier.wrapContentSize()
                    )
                }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClick(false)
                }
            ) {
                Text("Close")
            }
        },
    )

}
