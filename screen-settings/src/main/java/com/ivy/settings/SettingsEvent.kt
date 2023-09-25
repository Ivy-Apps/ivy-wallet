package com.ivy.settings

sealed interface SettingsEvent {
    data class SetCurrency(val newCurrency: String) : SettingsEvent
    data class SetName(val newName: String) : SettingsEvent
    data object ExportToCsv : SettingsEvent
    data object BackupData : SettingsEvent
    data object SwitchTheme : SettingsEvent
    data class SetLockApp(val lockApp: Boolean) : SettingsEvent
    data class SetShowNotifications(val showNotifications: Boolean) : SettingsEvent
    data class SetHideCurrentBalance(val hideCurrentBalance: Boolean) : SettingsEvent
    data class SetTransfersAsIncomeExpense(val treatTransfersAsIncomeExpense: Boolean) :
        SettingsEvent

    data class SetStartDateOfMonth(val startDate: Int) : SettingsEvent
    data object DeleteCloudUserData : SettingsEvent
    data object DeleteAllUserData : SettingsEvent
}