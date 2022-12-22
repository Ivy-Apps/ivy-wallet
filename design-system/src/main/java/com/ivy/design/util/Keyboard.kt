package com.ivy.design.util

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.view.WindowInsetsCompat

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
    fun initialize() {
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