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
fun SpacerV(
    height: Dp
) {
    Spacer(Modifier.height(height))
}

@Composable
fun SpacerH(
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
        SpacerV(height = heightAnimated)
    } else {
        val height by keyboardHeightState()
        SpacerV(height = height)
    }
}