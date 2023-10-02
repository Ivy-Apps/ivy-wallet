package com.ivy.settings

import com.ivy.base.legacy.Theme

data class SettingsState(
    val currencyCode: String,
    val name: String,
    val currentTheme: Theme,
    val lockApp: Boolean,
    val showNotifications: Boolean,
    val hideCurrentBalance: Boolean,
    val treatTransfersAsIncomeExpense: Boolean,
    val startDateOfMonth: String,
    val progressState: Boolean
)
