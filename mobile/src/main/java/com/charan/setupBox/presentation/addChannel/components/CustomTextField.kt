package com.charan.setupBox.presentation.addChannel.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value:String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,label:String,
    trailingIcon: @Composable (() -> Unit)? = null,
    ) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(text = label)
        },
        trailingIcon = trailingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    )


}

@Composable
fun PreviewButton(
    onClick: () -> Unit,
    isButtonEnabled: Boolean
) {
    FilledTonalIconButton(
        onClick = { onClick() },
        modifier = Modifier.padding(5.dp),
        enabled = isButtonEnabled
    ) {
        Icon(imageVector = Icons.Rounded.Preview, contentDescription = null)

    }
}