package com.ivy.design.l0_system

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable

@Immutable
data class IvyShapes(
    val square: CornerBasedShape,
    val rounded: CornerBasedShape,
    val full: CornerBasedShape,
)
