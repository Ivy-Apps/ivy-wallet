package com.ivy.design.util

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.data.IvyPadding

fun Modifier.paddingIvy(ivyPadding: IvyPadding?): Modifier = ivyPadding?.let {
    this.padding(
        top = it.top ?: 0.dp,
        bottom = it.bottom ?: 0.dp,
        start = it.start ?: 0.dp,
        end = it.end ?: 0.dp
    )
} ?: this

fun padding(
    top: Dp? = null,
    start: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null
): IvyPadding {
    return IvyPadding(
        top = top,
        bottom = bottom,
        start = start,
        end = end
    )
}

fun padding(
    horizontal: Dp? = null,
    vertical: Dp? = null
): IvyPadding {
    return IvyPadding(
        top = vertical,
        bottom = vertical,
        start = horizontal,
        end = horizontal
    )
}

fun padding(
    all: Dp? = null
): IvyPadding {
    return IvyPadding(
        top = all,
        bottom = all,
        start = all,
        end = all
    )
}

