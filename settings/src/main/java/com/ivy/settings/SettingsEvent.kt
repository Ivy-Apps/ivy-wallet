package com.ivy.settings

sealed interface SettingsEvent {
    object Back : SettingsEvent
    data class BaseCurrencyChange(val newCurrency: String) : SettingsEvent
    data class StartDayOfMonth(val startDayOfMonth: Int) : SettingsEvent
    data class HideBalance(val hideBalance: Boolean) : SettingsEvent
    data class AppLocked(val appLocked: Boolean) : SettingsEvent
}

