package com.ivy.core.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.frp.view.navigation.Screen
import java.util.*

val nav = Nav()

class Nav {
    var currentScreen: Screen? by mutableStateOf(null)
        private set

    private val backstack: Stack<BackstackItem> = Stack()
    private val onScreenBack: MutableMap<Screen, () -> BackResult> = mutableMapOf()

    fun handleBack(screen: Screen, onBack: () -> BackResult) {
        onScreenBack[screen] = onBack
    }

    fun navigateTo(screen: Screen) {
        backstack.add(BackstackItem.FullScreen(screen))
        currentScreen = screen
    }

    fun onBackPressed(): OnBackPressedResult {
        fun overlayBack(overlay: BackstackItem.Overlay) {
            when (overlay.onBack()) {
                BackResult.REMOVE -> backstack.pop()
                BackResult.KEEP -> {
                    // do nothing
                }
            }
        }

        if (backstack.isEmpty()) return OnBackPressedResult.CloseApp

        when (val top = backstack.peek()) {
            is BackstackItem.FullScreen -> {
                when (onScreenBack[top.screen]?.invoke()) {
                    null, BackResult.REMOVE -> {
                        // custom back handler not defined or doesn't want to keep the screen
                        // remove current screen from top
                        backstack.pop()
                    }
                    BackResult.KEEP -> {
                        // keep the current screen and do nothing
                        return OnBackPressedResult.DoNothing
                    }
                }

                when (val nextTop = backstack.peekSafe()) {
                    is BackstackItem.FullScreen -> {
                        // navigate to the next screen on top
                        currentScreen = nextTop.screen
                    }
                    is BackstackItem.Overlay -> {
                        backstack.pop() // overlays doesn't survive change
                        onBackPressed() // recurse until a screen to navigate to is found
                    }
                    null -> {
                        // nowhere to navigate to, no items left in the backstack
                        return OnBackPressedResult.CloseApp
                    }
                }
            }
            is BackstackItem.Overlay -> overlayBack(top)
        }

        return OnBackPressedResult.DoNothing
    }

    fun addToBackstack(overlay: BackstackItem.Overlay) {
        val alreadyAdded = backstack.any { (it as? BackstackItem.Overlay)?.id == overlay.id }
        if (!alreadyAdded) {
            backstack.add(overlay)
        }
    }
}

private fun <E> Stack<E>.peekSafe(): E? = if (isEmpty()) null else peek()
private fun <E> Stack<E>.popSafe(): E? = if (isEmpty()) null else pop()