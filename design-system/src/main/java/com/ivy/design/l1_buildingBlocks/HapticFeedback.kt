package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.hapticClickable(
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier {
    return if (hapticFeedbackEnabled) composed {
        // with haptic feedback
        val hapticFeedback = LocalHapticFeedback.current

        if (onLongClick != null) this.combinedClickable(
            enabled = enabled,
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            onLongClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            }
        ) else this.clickable(enabled = enabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    } else {
        // no haptic feedback
        if (onLongClick != null) this.combinedClickable(
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick,
        ) else this.clickable(enabled = enabled, onClick = onClick)
    }
}