package com.ivy.design.l1_buildingBlocks.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.color.asBrush
import com.ivy.design.util.paddingIvy
import com.ivy.design.util.thenWhen


// region constructor functions
fun solid(
    shape: Shape,
    color: Brush,
    padding: IvyPadding? = null
) = Background.Solid(
    color = color,
    shape = shape,
    padding = padding
)

fun solid(
    shape: Shape,
    color: Color,
    padding: IvyPadding? = null
) = Background.Solid(
    color = color.asBrush(),
    shape = shape,
    padding = padding
)

fun outlined(
    shape: Shape,
    color: Brush,
    width: Dp = 1.dp,
    padding: IvyPadding? = null
) = Background.Outlined(
    color = color,
    shape = shape,
    width = width,
    padding = padding
)

fun outlined(
    shape: Shape,
    color: Color,
    width: Dp = 1.dp,
    padding: IvyPadding? = null
) = Background.Outlined(
    color = color.asBrush(),
    shape = shape,
    width = width,
    padding = padding
)

fun solidWithBorder(
    shape: Shape,
    solid: Brush,
    borderColor: Brush,
    borderWidth: Dp = 1.dp,
    padding: IvyPadding? = null,
) = Background.SolidWithBorder(
    shape = shape,
    solid = solid,
    borderColor = borderColor,
    borderWidth = borderWidth,
    padding = padding
)

fun solidWithBorder(
    shape: Shape,
    solid: Color,
    borderColor: Color,
    borderWidth: Dp = 1.dp,
    padding: IvyPadding? = null,
) = Background.SolidWithBorder(
    shape = shape,
    solid = solid.asBrush(),
    borderColor = borderColor.asBrush(),
    borderWidth = borderWidth,
    padding = padding
)

fun none(): Background.None = Background.None
// endregion

@Immutable
sealed interface Background {
    @Immutable
    data class Solid internal constructor(
        val shape: Shape,
        val color: Brush,
        val padding: IvyPadding?
    ) : Background

    @Immutable
    data class Outlined internal constructor(
        val shape: Shape,
        val color: Brush,
        val width: Dp,
        val padding: IvyPadding?
    ) : Background

    @Immutable
    data class SolidWithBorder internal constructor(
        val shape: Shape,
        val solid: Brush,
        val borderColor: Brush,
        val borderWidth: Dp,
        val padding: IvyPadding?,
    ) : Background

    @Immutable
    object None : Background
}

fun Modifier.applyBackground(background: Background): Modifier {
    return thenWhen {
        when (background) {
            is Background.Solid -> {
                background(
                    brush = background.color,
                    shape = background.shape
                ).paddingIvy(background.padding)
            }
            is Background.Outlined -> {
                border(
                    brush = background.color,
                    width = background.width,
                    shape = background.shape
                ).paddingIvy(background.padding)
            }
            is Background.SolidWithBorder -> {
                background(brush = background.solid, shape = background.shape)
                    .border(
                        brush = background.borderColor,
                        width = background.borderWidth,
                        shape = background.shape
                    )
                    .paddingIvy(background.padding)
            }
            is Background.None -> null
        }
    }
}

fun Modifier.clipBackground(background: Background): Modifier {
    return thenWhen {
        when (background) {
            is Background.Solid -> {
                clip(background.shape)
            }
            is Background.Outlined -> {
                clip(background.shape)
            }
            is Background.SolidWithBorder -> {
                clip(background.shape)
            }
            is Background.None -> null
        }
    }
}