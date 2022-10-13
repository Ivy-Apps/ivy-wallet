package com.ivy.design.l3_ivyComponents

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
sealed interface Feeling {
    @Immutable
    object Positive : Feeling

    @Immutable
    object Negative : Feeling

    @Immutable
    object Neutral : Feeling

    @Immutable
    object Disabled : Feeling

    @Immutable
    data class Custom(val color: Color) : Feeling
}