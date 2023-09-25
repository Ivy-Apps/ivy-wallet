package com.ivy.settings

data class SettingsState(
    val currency: String,
    val accountName: String,
    val appTheme: String,
    val lockApp: Boolean,
    val showNotifications: Boolean,
    val hideCurrentBalance: Boolean,
    val transfersAsIncomeExpense: Boolean,
    val startDateOfMonth: String,
    val deleteAllUserData: Boolean
)
