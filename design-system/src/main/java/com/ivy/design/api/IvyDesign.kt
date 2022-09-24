package com.ivy.design.api

import androidx.compose.runtime.Immutable
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography
import com.ivy.design.l0_system.color.IvyColors

@Immutable
data class IvyDesign(
    val typography: IvyTypography,
    val colors: IvyColors,
    val colorsInverse: IvyColors,
    val shapes: IvyShapes,
)