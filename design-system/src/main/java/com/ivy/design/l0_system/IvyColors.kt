package com.ivy.design.l0_system

import androidx.compose.ui.graphics.Color
import javax.annotation.concurrent.Immutable

@Immutable
interface IvyColors {
    val pure: Color
    val pureInverse: Color

    val gray: Color
    val medium: Color
    val mediumInverse: Color

    val primary: Color
    val primary1: Color

    val green: Color
    val green1: Color

    val orange: Color
    val orange1: Color

    val red: Color
    val red1: Color
    val red1Inverse: Color

    val isLight: Boolean
}