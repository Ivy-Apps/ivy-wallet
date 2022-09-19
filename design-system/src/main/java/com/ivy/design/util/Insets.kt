package com.ivy.design.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

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