package com.charan.setupBox.presentation.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.charan.setupBox.presentation.common.components.CustomTooltipBox
import com.charan.setupBox.presentation.home.HomeEvent

@Composable
fun SyncStatusIndicators(
    isSyncing: Boolean,
    hasError: Boolean,
    errorMessage: String?,
    onSyncClick: () -> Unit
) {
    val syncRotation by rememberInfiniteTransition(label = "syncRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "syncRotationValue"
    )
    if (isSyncing) {
        CustomTooltipBox(
            tooltipText = "Sync in progress.",
            content = {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "Syncing",
                    modifier = Modifier.rotate(syncRotation)
                )
            },
            toolTipPosition = TooltipAnchorPosition.Below
        )
    } else if (hasError) {

        CustomTooltipBox(
            tooltipText = errorMessage ?: "",
            content = {
                IconButton(
                    onClick = {
                        onSyncClick()
                    },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        imageVector = Icons.Filled.SyncProblem,
                        contentDescription = "Sync Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

            },
            toolTipPosition = TooltipAnchorPosition.Below
        )
    }

}