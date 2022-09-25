package com.ivy.design.l0_system.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class IvyColors(
    // region Monochrome
    val pure: Color,
    val neutral: Color,
    val medium: Color,
    // endregion

    // region Dynamic
    val primary: Color,
    val primaryP1: Color,
    val primaryP2: Color,

    val red: Color,
    val redP1: Color,
    val redP2: Color,

    val orange: Color,
    val orangeP1: Color,
    val orangeP2: Color,

    val yellow: Color,
    val yellowP1: Color,
    val yellowP2: Color,

    val green: Color,
    val greenP1: Color,
    val greenP2: Color,

    val blue: Color,
    val blueP1: Color,
    val blueP2: Color,

    val purple: Color,
    val purpleP1: Color,
    val purpleP2: Color,

    val isLight: Boolean,

    val transparent: Color = Transparent
)