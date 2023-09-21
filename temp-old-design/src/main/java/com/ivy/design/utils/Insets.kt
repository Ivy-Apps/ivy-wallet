package com.ivy.design.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun windowInsets(): WindowInsetsCompat {
    val rootView = LocalView.current
    return WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
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
