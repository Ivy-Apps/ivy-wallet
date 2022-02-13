package com.ivy.design.l0_system

enum class Theme {
    LIGHT, DARK;

    fun inverted(): Theme {
        return when (this) {
            LIGHT -> DARK
            DARK -> LIGHT
        }
    }
}