package com.ivy.design.l0_system

import androidx.compose.ui.text.TextStyle
import javax.annotation.concurrent.Immutable

@Immutable
interface IvyTypography {
    val h1: TextStyle
    val h2: TextStyle
    val b1: TextStyle
    val b2: TextStyle
    val c: TextStyle

    val nH1: TextStyle
    val nH2: TextStyle
    val nB1: TextStyle
    val nB2: TextStyle
    val nC: TextStyle
}