package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

fun Modifier.hapticClickable(
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    onClick: () -> Unit
): Modifier = if (hapticFeedbackEnabled) composed {
    val hapticFeedback = LocalHapticFeedback.current

    this.clickable(enabled = enabled) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
    }
} else this.clickable(enabled = enabled, onClick = onClick)