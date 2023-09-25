package com.ivy.settings

data class SettingsState(
    val currency: String,
    val name: String,
    val currentTheme: String,
    val lockApp: Boolean,
    val showNotifications: Boolean,
    val hideCurrentBalance: Boolean,
    val transfersAsIncomeExpense: Boolean,
    val startDateOfMonth: String
)
