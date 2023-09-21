package com.ivy.design.l1_buildingBlocks.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.asBrush
import com.ivy.design.utils.ivyPadding
import com.ivy.design.utils.thenWhen

@Deprecated("Old design system. Use `:ivy-design` and Material3")
sealed class Background {
    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    data class Solid(
        val color: Brush,
        val shape: Shape,
        val padding: IvyPadding
    ) : Background() {
        constructor(
            color: Color,
            shape: Shape,
            padding: IvyPadding
        ) : this(
            color = color.asBrush(),
            shape = shape,
            padding = padding
        )
    }

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    data class Outlined(
        val color: Brush,
        val width: Dp = 1.dp,
        val shape: Shape,
        val padding: IvyPadding
    ) : Background() {
        constructor(
            color: Color,
            width: Dp = 1.dp,
            shape: Shape,
            padding: IvyPadding
        ) : this(
            color = color.asBrush(),
            width = width,
            shape = shape,
            padding = padding
        )
    }

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    object None : Background()
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.background(background: Background): Modifier {
    return thenWhen {
        when (background) {
            is Background.Solid -> {
                background(
                    brush = background.color,
                    shape = background.shape
                ).ivyPadding(background.padding)
            }
            is Background.Outlined -> {
                border(
                    brush = background.color,
                    width = background.width,
                    shape = background.shape
                ).ivyPadding(background.padding)
            }
            is Background.None -> null
        }
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.clipBackground(background: Background): Modifier {
    return thenWhen {
        when (background) {
            is Background.Solid -> {
                clip(background.shape)
            }
            is Background.Outlined -> {
                clip(background.shape)
            }
            is Background.None -> null
        }
    }
}
