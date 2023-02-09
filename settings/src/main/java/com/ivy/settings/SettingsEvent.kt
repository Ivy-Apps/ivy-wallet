package com.ivy.settings

import android.net.Uri

sealed interface SettingsEvent {
    object Back : SettingsEvent
    data class BaseCurrencyChange(val newCurrency: String) : SettingsEvent
    data class StartDayOfMonth(val startDayOfMonth: Int) : SettingsEvent
    data class LanguageChange(val languageCode: String) : SettingsEvent

    data class HideBalance(val hideBalance: Boolean) : SettingsEvent
    data class AppLocked(val appLocked: Boolean) : SettingsEvent

    object ImportOldData : SettingsEvent

    object MountDrive : SettingsEvent

    object AddFrame : SettingsEvent
    object NukeAccCache : SettingsEvent
    object ExchangeRates : SettingsEvent

    data class BackupData(val backupLocation: Uri) : SettingsEvent
}

