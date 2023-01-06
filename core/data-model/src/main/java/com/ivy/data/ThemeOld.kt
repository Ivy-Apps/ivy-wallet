package com.ivy.data

@Deprecated("move in design-system")
enum class ThemeOld {
    LIGHT, DARK, AUTO;

    fun inverted(): ThemeOld {
        return when (this) {
            LIGHT -> DARK
            DARK -> LIGHT
            AUTO -> AUTO
        }
    }
}