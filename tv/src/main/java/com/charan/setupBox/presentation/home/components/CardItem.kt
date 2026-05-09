package com.charan.setupBox.presentation.home.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardContainerDefaults
import androidx.tv.material3.CardDefaults

import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer

import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.charan.shared.utils.AppUtils

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CardItem(
    channelPhoto : String,
    channelName : String,
    modifier: Modifier,
    onClick : () -> Unit,){
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.titleLarge.copy(color = AppUtils.getTextColorForPlaceholder(), fontWeight = FontWeight.Bold)

    StandardCardContainer(
        modifier = Modifier
            .width(300.dp)
            .padding(25.dp)
            .then(modifier),
        imageCard = {
            Card(
                onClick = {
                    onClick()
                },
                interactionSource = it,
                colors = CardDefaults.colors(containerColor = Color.Transparent),
                border = CardDefaults.border(focusedBorder = Border(
                    BorderStroke(2.dp,Color.White),
                    inset = 4.dp
                )),


            ) {
                if(channelPhoto.isNullOrEmpty().not()) {
                    AsyncImage(
                        model = channelPhoto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                        contentScale = ContentScale.Crop,

                        contentDescription = null
                    )
                } else{
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(CardDefaults.HorizontalImageAspectRatio)) {
                        val text = channelName.toString()
                        val textLayoutResult = textMeasurer.measure(text = text, style = textStyle)
                        val x = (size.width - textLayoutResult.size.width) / 2
                        val y = (size.height - textLayoutResult.size.height) / 2

                        drawRect(AppUtils.getColorForPlaceHolderBackground(),)
                        drawText(
                            textMeasurer,text,
                            style = textStyle,
                            topLeft = Offset(x, y),


                            )


                    }
                }
            }

        },
        title = {
            Text(
                text = channelName.toString(),
                modifier = Modifier.padding(top = 14.dp)
            )
        },



        )
}