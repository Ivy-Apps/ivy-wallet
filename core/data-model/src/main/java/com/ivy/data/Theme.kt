package com.ivy.data

@Deprecated("move in design-system")
enum class Theme {
    LIGHT, DARK, AUTO;

    fun inverted(): Theme {
        return when (this) {
            LIGHT -> DARK
            DARK -> LIGHT
            AUTO -> AUTO
        }
    }
}