package com.ivy.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

// region Insets Compose
/**
 * @return system's bottom inset (nav buttons or bottom nav)
 */
@Composable
fun systemPaddingBottom(): Dp {
    val rootView = LocalView.current
    val densityScope = LocalDensity.current
    return remember(rootView) {
        val insetPx =
            WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
                .getInsets(WindowInsetsCompat.Type.navigationBars())
                .bottom
        with(densityScope) { insetPx.toDp() }
    }
}

@Composable
fun keyboardPadding(): Dp {
    val rootView = LocalView.current
    val insetPx =
        WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
            .getInsets(
                WindowInsetsCompat.Type.ime() or
                        WindowInsetsCompat.Type.navigationBars()
            )
            .bottom
    return insetPx.toDensityDp()
}
// endregion


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