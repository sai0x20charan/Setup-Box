package com.charan.setupBox.presentation.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AvatarImage(
    imageUrl : String?
) {
    Box(modifier=Modifier.graphicsLayer {
        this.shape= CircleShape
        this.clip=true

    }){
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier=Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

    }
}