package com.ivy.design.l1_buildingBlocks.data

import androidx.compose.ui.unit.Dp
import javax.annotation.concurrent.Immutable

@Immutable
data class IvyPadding(
    val top: Dp?,
    val bottom: Dp?,
    val start: Dp?,
    val end: Dp?
)