package com.ivy.legacy.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ivy.design.l0_system.Gradient
import com.ivy.navigation.navigation


@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun windowInsets(): WindowInsetsCompat {
    val rootView = LocalView.current
    return WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun statusBarInset(): Int {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun navigationBarInset(): Int {
    return navigationBarInsets().bottom
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun navigationBarInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun keyboardOnlyWindowInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(
        WindowInsetsCompat.Type.ime()
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun <T> densityScope(densityScope: @Composable Density.() -> T): T {
    return with(LocalDensity.current) { densityScope() }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.thenIf(condition: Boolean, thanModifier: @Composable Modifier.() -> Modifier): Modifier = composed {
    if (condition) {
        this.thanModifier()
    } else {
        this
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@SuppressLint("ComposableNaming")
@Composable
fun onScreenStart(
    cleanUp: () -> Unit = {},
    start: () -> Unit
) {
    DisposableEffect(navigation().currentScreen) {
        start()
        onDispose { cleanUp() }
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.consumeClicks() = clickableNoIndication {
    // consume click
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
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.15f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 16.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 8.dp
) = this.drawBehind {
    val transparentColor = android.graphics.Color.toArgb(color.copy(alpha = 0.0f).value.toLong())
    val shadowColor = android.graphics.Color.toArgb(color.copy(alpha = alpha).value.toLong())
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun selectEndTextFieldValue(text: String?) = TextFieldValue(
    text = text ?: "",
    selection = TextRange(text?.length ?: 0)
)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Dp.toDensityPx() = densityScope { toPx() }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Int.toDensityDp() = densityScope { toDp() }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Float.toDensityDp() = densityScope { toDp() }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun openUrl(uriHandler: UriHandler, url: String) {
    uriHandler.openUri(url)
}
