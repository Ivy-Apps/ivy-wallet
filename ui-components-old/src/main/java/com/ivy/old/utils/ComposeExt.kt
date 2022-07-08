package com.ivy.wallet.utils

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
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.ui.theme.Gradient

fun Modifier.horizontalGradientBackground(
    gradient: Gradient
) = drawWithCache {
    // Use drawWithCache modifier to create and cache the gradient once size is known or changes.
    onDrawBehind {
        drawRect(
            brush = Brush.horizontalGradient(
                startX = 0.0f,
                endX = size.width,
                colors = listOf(gradient.startColor, gradient.endColor)
            )
        )
    }
}

@Composable
fun windowInsets(): WindowInsetsCompat {
    val rootView = LocalView.current
    return WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
}

@Composable
fun systemWindowInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.navigationBars())
}

@Composable
fun statusBarInset(): Int {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
}

@Composable
fun navigationBarInset(): Int {
    return navigationBarInsets().bottom
}

@Composable
fun navigationBarInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
}


@Composable
fun keyboardNavigationWindowInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(
        WindowInsetsCompat.Type.ime()
                or WindowInsetsCompat.Type.systemBars()
    )
}

@Composable
fun keyboardOnlyWindowInsets(): Insets {
    val windowInsets = windowInsets()
    return windowInsets.getInsets(
        WindowInsetsCompat.Type.ime()
    )
}


@Composable
fun <T> densityScope(densityScope: @Composable Density.() -> T): T {
    return with(LocalDensity.current) { densityScope() }
}

fun <T> MutableState<T>.triggerUpdate() {
    try {
        this.value = value
    } catch (e: Exception) {
    }
}

@Composable
fun <R, T : R> LiveData<T>.observeAsNeverEqualState(initial: R): State<R> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial, policy = neverEqualPolicy()) }

    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T> { state.value = it }
        observe(lifecycleOwner, observer)
        onDispose { removeObserver(observer) }
    }
    return state
}

fun Modifier.thenIf(condition: Boolean, thanModifier: @Composable Modifier.() -> Modifier)
        : Modifier = composed {
    if (condition) {
        this.thanModifier()
    } else this
}

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

fun Modifier.consumeClicks() = clickableNoIndication {
    //consume click
}

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

fun selectEndTextFieldValue(text: String?) = TextFieldValue(
    text = text ?: "",
    selection = TextRange(text?.length ?: 0)
)

@Composable
fun Dp.toDensityPx() = densityScope { toPx() }

@Composable
fun Int.toDensityDp() = densityScope { toDp() }

@Composable
fun Float.toDensityDp() = densityScope { toDp() }

fun openUrl(uriHandler: UriHandler, url: String) {
    uriHandler.openUri(url)
}