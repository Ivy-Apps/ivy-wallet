package com.ivy.wallet.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val CORNER_RADIUS_32 = 32.dp
val CORNER_RADIUS_24 = 24.dp
val CORNER_RADIUS_16 = 16.dp
val CORNER_RADIUS_8 = 8.dp


val Shapes = IvyShapes(
    rounded32 = RoundedCornerShape(CORNER_RADIUS_32),
    rounded32Top = RoundedCornerShape(
        topStart = CORNER_RADIUS_32,
        topEnd = CORNER_RADIUS_32
    ),
    rounded24 = RoundedCornerShape(CORNER_RADIUS_24),
    rounded24Top = RoundedCornerShape(
        topStart = CORNER_RADIUS_24,
        topEnd = CORNER_RADIUS_24
    ),
    rounded20 = RoundedCornerShape(20.dp),
    rounded16 = RoundedCornerShape(CORNER_RADIUS_16),
    rounded16Top = RoundedCornerShape(
        topStart = CORNER_RADIUS_16,
        topEnd = CORNER_RADIUS_16
    ),
    rounded8 = RoundedCornerShape(CORNER_RADIUS_8),
    roundedFull = RoundedCornerShape(percent = 50)
)

data class IvyShapes(
    val rounded32: CornerBasedShape,
    val rounded32Top: CornerBasedShape,
    val rounded24: CornerBasedShape,
    val rounded24Top: CornerBasedShape,
    val rounded20: CornerBasedShape,
    val rounded16: CornerBasedShape,
    val rounded16Top: CornerBasedShape,
    val rounded8: CornerBasedShape,
    val roundedFull: CornerBasedShape
)