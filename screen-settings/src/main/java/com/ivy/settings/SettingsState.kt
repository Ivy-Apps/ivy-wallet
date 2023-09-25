package com.ivy.settings

import com.ivy.core.datamodel.legacy.Theme

data class SettingsState(
    val currencyCode: String,
    val name: String,
    val currentTheme: Theme,
    val lockApp: Boolean,
    val showNotifications: Boolean,
    val hideCurrentBalance: Boolean,
    val transfersAsIncomeExpense: Boolean,
    val startDateOfMonth: String,
    val progressState: Boolean
)
