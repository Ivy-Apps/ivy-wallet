package com.ivy.design.l0_system

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class IvyColors(
    // region Monochrome
    val pure: Color,
    val pureInverse: Color,
    val gray: Color,
    val medium: Color,
    val mediumInverse: Color,
    // endregion

    // region Dynamic
    // TODO: Define dynamic colors better
    val primary: Color,
    val primary1: Color,

    val green: Color,
    val green1: Color,

    val orange: Color,
    val orange1: Color,

    val red: Color,
    val red1: Color,
    val red1Inverse: Color,
    // endregion

    val isLight: Boolean,
)