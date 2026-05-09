package com.charan.setupBox.presentation.common.components

import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable

@Composable
fun CustomTooltipBox(
    tooltipText: String,
    content: @Composable () -> Unit,
    toolTipPosition: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    state: TooltipState = rememberTooltipState()

) {
    TooltipBox(
        tooltip = {
            RichTooltip(
                text = { Text(tooltipText) },
            )
        },
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(toolTipPosition),
        state = rememberTooltipState(),
        content = { content() }
    )
}