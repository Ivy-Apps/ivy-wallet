package com.ivy.menu

import com.ivy.data.Theme

sealed interface MoreMenuEvent {
    object CategoriesClick : MoreMenuEvent
    object SettingsClick : MoreMenuEvent
    data class ThemeChange(val theme: Theme) : MoreMenuEvent
    object Close : MoreMenuEvent
}