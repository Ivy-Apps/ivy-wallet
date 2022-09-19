package com.ivy.design

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Deprecated("bad idea - don't use it")
abstract class IvyContext {
    var theme: com.ivy.data.Theme by mutableStateOf(com.ivy.data.Theme.LIGHT)
        private set

    var screenWidth: Int = -1
        get() {
            return if (field > 0) field else throw IllegalStateException("screenWidth not initialized")
        }
    var screenHeight: Int = -1
        get() {
            return if (field > 0) field else throw IllegalStateException("screenHeight not initialized")
        }

    fun switchTheme(theme: com.ivy.data.Theme) {
        this.theme = theme
    }
}