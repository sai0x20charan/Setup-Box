package com.charan.setupBox.presentation.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.charan.setupBox.utils.AppUtils

@Composable
fun ThumbnailImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    width: Dp = 120.dp,
    height: Dp = 70.dp,
    contentDescription: String? = null,
    fallBackText: String = "No Image",
    cornerRadius: Dp = 10.dp,
    elevation: Dp = 4.dp,
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(shape)
    ) {
        if (imageUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = {
                    FallbackCanvas(
                        fallBackText = fallBackText,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            )
        } else {
            FallbackCanvas(
                fallBackText = fallBackText,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun FallbackCanvas(
    fallBackText: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMediumEmphasized.copy(
        color = contentColorFor(backgroundColor),
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    )

    Canvas(
        modifier = modifier
    ) {
        val textLayoutResult = textMeasurer.measure(text = fallBackText, style = textStyle)
        val x = (size.width - textLayoutResult.size.width) / 2
        val y = (size.height - textLayoutResult.size.height) / 2
        drawRect(backgroundColor)
        drawText(
            textMeasurer, fallBackText, style = textStyle, topLeft = Offset(x, y)
        )
    }
}