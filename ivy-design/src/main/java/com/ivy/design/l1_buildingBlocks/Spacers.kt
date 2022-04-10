package com.ivy.design.l1_buildingBlocks

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.ivy.design.utils.keyboardHeightState
import com.ivy.design.utils.keyboardHeightStateAnimated

@Composable
fun SpacerVer(
    height: Dp
) {
    Spacer(Modifier.height(height))
}

@Composable
fun SpacerHor(
    width: Dp
) {
    Spacer(Modifier.width(width))
}

@Composable
fun RowScope.SpacerWeight(
    weight: Float
) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun ColumnScope.SpacerWeight(
    weight: Float
) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun SpacerKeyboardHeight(
    animation: AnimationSpec<Dp>? = null
) {
    if (animation != null) {
        val heightAnimated by keyboardHeightStateAnimated(
            animationSpec = animation
        )
        SpacerVer(height = heightAnimated)
    } else {
        val height by keyboardHeightState()
        SpacerVer(height = height)
    }
}