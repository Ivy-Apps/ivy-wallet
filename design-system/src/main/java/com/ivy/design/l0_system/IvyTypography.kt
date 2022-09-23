package com.ivy.design.l0_system

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

@Immutable
data class IvyTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val b1: TextStyle,
    val b2: TextStyle,
    val c: TextStyle,

    // TODO: Rename to sH1, sH2...
    val nH1: TextStyle,
    val nH2: TextStyle,
    val nB1: TextStyle,
    val nB2: TextStyle,
    val nC: TextStyle,
)