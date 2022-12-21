package com.ivy.core.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer

fun LazyListScope.lastItemSpacerVertical(
    height: Dp = 24.dp,
) {
    item(key = "last_item_spacer") {
        SpacerVer(height = height)
    }
}

fun LazyListScope.lastItemSpacerHorizontal(
    width: Dp = 24.dp,
) {
    item(key = "last_item_spacer") {
        SpacerHor(width = width)
    }
}