package com.ivy.design.l0_system

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape

abstract class IvyShapes {
    abstract val r1: CornerBasedShape
    abstract val r1Top: CornerBasedShape
    abstract val r1Bot: CornerBasedShape

    abstract val r2: CornerBasedShape
    abstract val r2Top: CornerBasedShape
    abstract val r2Bot: CornerBasedShape

    abstract val r3: CornerBasedShape
    abstract val r3Top: CornerBasedShape
    abstract val r3Bot: CornerBasedShape

    abstract val r4: CornerBasedShape
    abstract val r4Top: CornerBasedShape
    abstract val r4Bot: CornerBasedShape

    val rFull: CornerBasedShape = RoundedCornerShape(percent = 50)
    val circle = CircleShape
}