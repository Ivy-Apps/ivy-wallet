package com.ivy.design.l3_ivyComponents

import androidx.compose.ui.graphics.Color

sealed interface Feeling {
    object Positive : Feeling
    object Negative : Feeling
    object Neutral : Feeling
    object Disabled : Feeling
    data class Custom(val color: Color) : Feeling
}