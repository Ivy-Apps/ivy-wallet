package com.ivy.design.l0_system

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
