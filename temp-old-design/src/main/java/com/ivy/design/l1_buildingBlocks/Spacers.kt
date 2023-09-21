package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun SpacerVer(
    height: Dp
) {
    Spacer(Modifier.height(height))
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun SpacerHor(
    width: Dp
) {
    Spacer(Modifier.width(width))
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun RowScope.SpacerWeight(
    weight: Float
) {
    Spacer(Modifier.weight(weight))
}