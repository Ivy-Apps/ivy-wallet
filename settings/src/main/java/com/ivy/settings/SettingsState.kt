package com.ivy.settings

import com.ivy.settings.data.BackupImportState

data class SettingsState(
    val baseCurrency: String,
    val startDayOfMonth: Int,
    val hideBalance: Boolean,
    val appLocked: Boolean,
    val driveMounted: Boolean,
    val importOldData: BackupImportState,
)