package com.charan.setupBox.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text

@Composable
fun TitleText(title : String){
    Text(
        title,
        modifier = Modifier.padding(start = 20.dp, top = 50.dp)
    )
}