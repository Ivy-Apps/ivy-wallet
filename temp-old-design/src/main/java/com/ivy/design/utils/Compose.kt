package com.ivy.design.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun <T> densityScope(densityScope: @Composable Density.() -> T): T {
    return with(LocalDensity.current) { densityScope() }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.thenIf(
    condition: Boolean,
    otherModifier: @Composable Modifier.() -> Modifier
): Modifier = composed {
    // Cannot use Modifier#then() because it stacks the previous modifiers multiple times
    if (condition) {
        this.otherModifier()
    } else {
        this
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.thenWhen(
    logic: @Composable Modifier.() -> Modifier?
): Modifier = composed {
    this.logic() ?: this
}


@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.clickableNoIndication(
    onClick: () -> Unit
): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick,
        role = null,
        indication = null
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@SuppressLint("ComposableNaming")
@Composable
fun onEvent(
    eventKey: Any = Unit,
    cleanUp: () -> Unit = {},
    logic: () -> Unit
) {
    DisposableEffect(eventKey) {
        logic()
        onDispose { cleanUp() }
    }
}


@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Int.toDensityDp() = densityScope { toDp() }