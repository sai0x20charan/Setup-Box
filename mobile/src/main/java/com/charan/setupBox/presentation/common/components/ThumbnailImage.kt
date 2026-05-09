package com.charan.setupBox.presentation.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.charan.setupBox.utils.AppUtils
import kotlin.collections.get

@Composable
fun ThumbnailImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    width : Int= 120,
    height : Int = 70,
    contentDescription: String? = null,
    fallBackText : String = "No Image"

) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.titleLarge.copy(
        color = AppUtils.getTextColorForPlaceholder(),
        fontWeight = FontWeight.Bold
    )
    if(imageUrl.isNotEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(width.dp).height(height.dp).clip(RoundedCornerShape(10.dp))
                .then(modifier)
        )
    } else{
        Canvas(
            modifier = Modifier.width(120.dp).height(71.dp).clip(RoundedCornerShape(10.dp))
        ) {
            val text = fallBackText
            val textLayoutResult = textMeasurer.measure(text = text, style = textStyle)
            val x = (size.width - textLayoutResult.size.width) / 2
            val y = (size.height - textLayoutResult.size.height) / 2
            drawRect(AppUtils.getColorForPlaceHolderBackground())
            drawText(textMeasurer, text, style = textStyle, topLeft = Offset(x, y))
        }

    }


}