package com.charan.setupBox.presentation.addChannel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charan.setupBox.presentation.common.components.ThumbnailImage

@Composable
fun PreviewAlertBox(
    imageLink:String,
    onClick:(Boolean)->Unit
) {
    CustomAlertDialog(
        title = "Preview",
        onDismiss = {
            onClick(false)
        },
        onConfirm = {
            onClick(true)
        },
        confirmButtonText = "Done"
    ) {
            Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center) {
                ThumbnailImage(
                    imageUrl = imageLink,
                    modifier = Modifier.wrapContentSize()
                )
            }
    }

}
