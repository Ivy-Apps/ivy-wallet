package com.ivy.legacy.frp

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.ivy.navigation.navigation

@Deprecated("Legacy code. Don't use it, please.")
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