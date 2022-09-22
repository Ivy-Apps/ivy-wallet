package com.ivy.design.api

import androidx.compose.runtime.Immutable
import com.ivy.design.l0_system.IvyColors
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography

@Immutable
data class IvyDesign(
    val typography: IvyTypography,
    val colors: IvyColors,
    val shapes: IvyShapes,
)