package com.ivy.design.util

import android.view.View
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout

@Composable
fun keyboardShiftAnimated(): State<Dp> {
    val systemBottomPadding = systemPaddingBottom()
    val keyboardShown by keyboardShownState()
    val keyboardShownInset = keyboardPadding()
    return animateDpAsState(
        targetValue = if (keyboardShown)
            keyboardShownInset else systemBottomPadding,
    )
}

@Composable
fun keyboardShownState(): State<Boolean> {
    val keyboardOpen = remember { mutableStateOf(false) }
    val rootView = LocalView.current

    DisposableEffect(Unit) {
        val keyboardListener = {
            // check keyboard state after this layout
            val isOpenNew = isKeyboardOpen(rootView)

            // since the observer is hit quite often, only callback when there is a change.
            if (isOpenNew != keyboardOpen.value) {
                keyboardOpen.value = isOpenNew
            }
        }

        rootView.doOnLayout {
            // get initial state of keyboard
            keyboardOpen.value = isKeyboardOpen(rootView)

            // whenever the layout resizes/changes, callback with the state of the keyboard.
            rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        }

        onDispose {
            // stop keyboard updates
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(keyboardListener)
        }
    }

    return keyboardOpen
}


fun isKeyboardOpen(rootView: View): Boolean {
    return try {
        WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
            .isVisible(WindowInsetsCompat.Type.ime())
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

class KeyboardController {
    private val state = mutableStateOf(0)

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun wire() {
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(state.value) {
            if (state.value > 0) {
                keyboardController?.show()
            } else {
                keyboardController?.hide()
            }
        }
    }

    fun show() {
        if (state.value > 0) {
            state.value = state.value + 1
        } else {
            state.value = 1
        }
    }

    fun hide() {
        if (state.value < 0) {
            state.value = state.value - 1
        } else {
            state.value = -1
        }
    }
}