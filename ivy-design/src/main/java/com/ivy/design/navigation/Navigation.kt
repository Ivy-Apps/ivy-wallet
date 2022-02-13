package com.ivy.design.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.*

class Navigation {
    var currentScreen: Screen? by mutableStateOf(null)
        private set

    private val backStack: Stack<Screen> = Stack()
    private var lastScreen: Screen? = null

    var modalBackHandling: Stack<ModalBackHandler> = Stack()

    data class ModalBackHandler(
        val id: UUID,
        val onBackPressed: () -> Boolean
    )

    fun lastModalBackHandlerId(): UUID? {
        return if (modalBackHandling.isEmpty()) {
            null
        } else {
            modalBackHandling.peek().id
        }
    }

    var onBackPressed: MutableMap<Screen, () -> Boolean> = mutableMapOf()

    fun navigateTo(screen: Screen, allowBackStackStore: Boolean = true) {
        if (lastScreen != null && allowBackStackStore) {
            backStack.push(lastScreen)
        }
        switchScreen(screen)
    }

    fun resetBackStack() {
        while (!backStackEmpty()) {
            popBackStack()
        }
        lastScreen = null
    }

    fun backStackEmpty() = backStack.empty()

    fun popBackStackSafe() {
        if (!backStackEmpty()) {
            popBackStack()
        }
    }

    private fun popBackStack() {
        backStack.pop()
    }

    fun onBackPressed(): Boolean {
        if (modalBackHandling.isNotEmpty()) {
            return modalBackHandling.peek().onBackPressed()
        }
        val specialHandling = onBackPressed.getOrDefault(currentScreen, { false }).invoke()
        return specialHandling || back()
    }

    fun back(): Boolean {
        if (!backStack.empty()) {
            switchScreen(backStack.pop())
            return true
        }
        return false
    }

    fun lastBackstackScreen(): Screen? {
        return if (!backStackEmpty()) {
            backStack.peek()
        } else {
            null
        }
    }

    private fun switchScreen(screen: Screen) {
        this.currentScreen = screen
        lastScreen = screen
    }
}