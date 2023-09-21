package com.ivy.design.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout


@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@SuppressLint("ComposableNaming")
@Composable
fun hideKeyboard() {
    LocalView.current.hideKeyboard()
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun View.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun keyboardVisibleState(): State<Boolean> {
    val rootView = LocalView.current

    val keyboardVisible = remember {
        mutableStateOf(false)
    }

    onEvent {
        rootView.addKeyboardListener {
            keyboardVisible.value = it
        }
    }

    return keyboardVisible
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun View.addKeyboardListener(keyboardCallback: (visible: Boolean) -> Unit) {
    doOnLayout {
        // get init state of keyboard
        var keyboardVisible = isKeyboardOpen(this)

        // callback as soon as the layout is set with whether the keyboard is open or not
        keyboardCallback(keyboardVisible)

        // whenever the layout resizes/changes, callback with the state of the keyboard.
        viewTreeObserver.addOnGlobalLayoutListener {
            val keyboardUpdateCheck = isKeyboardOpen(this)
            // since the observer is hit quite often, only callback when there is a change.
            if (keyboardUpdateCheck != keyboardVisible) {
                keyboardCallback(keyboardUpdateCheck)
                keyboardVisible = keyboardUpdateCheck
            }
        }
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun isKeyboardOpen(rootView: View): Boolean {
    return try {
        WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
            .isVisible(WindowInsetsCompat.Type.ime())
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
