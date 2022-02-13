package com.ivy.design.l0_system

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape

abstract class IvyShapes {
    abstract val r1: CornerBasedShape
    abstract val r1top: CornerBasedShape
    abstract val r1bot: CornerBasedShape

    abstract val r2: CornerBasedShape
    abstract val r2top: CornerBasedShape
    abstract val r2bot: CornerBasedShape

    abstract val r3: CornerBasedShape
    abstract val r3top: CornerBasedShape
    abstract val r3bot: CornerBasedShape

    abstract val r4: CornerBasedShape
    abstract val r4top: CornerBasedShape
    abstract val r4bot: CornerBasedShape

    val rFull: CornerBasedShape = RoundedCornerShape(percent = 50)
    val circle = CircleShape
}