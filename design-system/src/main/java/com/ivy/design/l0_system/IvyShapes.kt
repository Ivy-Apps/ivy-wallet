package com.ivy.design.l0_system

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable

@Immutable
data class IvyShapes(
    // TODO: Define shapes better
    val r1: CornerBasedShape,
    val r1Top: CornerBasedShape,
    val r1Bot: CornerBasedShape,

    val r2: CornerBasedShape,
    val r2Top: CornerBasedShape,
    val r2Bot: CornerBasedShape,

    val r3: CornerBasedShape,
    val r3Top: CornerBasedShape,
    val r3Bot: CornerBasedShape,

    val r4: CornerBasedShape,
    val r4Top: CornerBasedShape,
    val r4Bot: CornerBasedShape,

    val rFull: CornerBasedShape = RoundedCornerShape(percent = 50),
    val circle: CornerBasedShape = CircleShape,
)