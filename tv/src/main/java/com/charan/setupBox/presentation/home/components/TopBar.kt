package com.charan.setupBox.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme

@Composable
fun HomeScreenTopBar(
    onRefresh: () -> Unit,
    onModalSheetOpen: ()->Unit,
    profilePicURL : String


) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            horizontalAlignment = Alignment.End
        ) {
            LazyRow {
                item {
                    Card(
                        onClick = { onRefresh() },
                        modifier = Modifier
                            .aspectRatio(CardDefaults.SquareImageAspectRatio)
                            .padding(10.dp)
                            .size(40.dp),
                        border = CardDefaults.border(
                            focusedBorder = Border(
                                border = BorderStroke(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        ),
                        shape = CardDefaults.shape(shape = CircleShape),
                        scale = CardDefaults.scale(focusedScale = 1.05f),


                        ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {


                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier
                            )
                        }
                    }
                    AvatarImageCard(
                        imageUrl = profilePicURL
                    ) {
                        onModalSheetOpen()
                    }
                }
            }
        }
    }

}


