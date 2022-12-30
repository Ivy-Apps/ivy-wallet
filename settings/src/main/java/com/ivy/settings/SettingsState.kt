package com.ivy.settings

data class SettingsState(
    val baseCurrency: String,
    val startDayOfMonth: Int,
    val hideBalance: Boolean,
    val appLocked: Boolean
)