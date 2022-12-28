package com.ivy.settings

sealed interface SettingsEvent {
    object Back : SettingsEvent
}