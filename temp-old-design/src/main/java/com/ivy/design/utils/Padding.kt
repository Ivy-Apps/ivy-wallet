package com.ivy.design.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.data.IvyPadding

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.ivyPadding(ivyPadding: IvyPadding): Modifier {
    return this.padding(
        top = ivyPadding.top ?: 0.dp,
        bottom = ivyPadding.bottom ?: 0.dp,
        start = ivyPadding.start ?: 0.dp,
        end = ivyPadding.end ?: 0.dp
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
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

@Deprecated("Old design system. Use `:ivy-design` and Material3")
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

@Deprecated("Old design system. Use `:ivy-design` and Material3")
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
