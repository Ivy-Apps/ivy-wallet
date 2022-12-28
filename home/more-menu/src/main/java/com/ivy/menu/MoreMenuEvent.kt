package com.ivy.menu

sealed interface MoreMenuEvent {
    object CategoriesClick : MoreMenuEvent
    object SettingsClick : MoreMenuEvent
}