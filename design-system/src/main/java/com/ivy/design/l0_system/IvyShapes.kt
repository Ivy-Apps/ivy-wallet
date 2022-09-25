package com.ivy.design.l0_system

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable

@Immutable
data class IvyShapes(
    val squared: CornerBasedShape,
    val squaredTop: CornerBasedShape,
    val squaredBottom: CornerBasedShape,

    val rounded: CornerBasedShape,
    val roundedTop: CornerBasedShape,
    val roundedBottom: CornerBasedShape,

    val fullyRounded: CornerBasedShape,

    val circle: RoundedCornerShape = CircleShape
)
