package com.ivy.settings

import com.ivy.settings.data.BackupImportState
import com.ivy.settings.data.Language

data class SettingsState(
    val baseCurrency: String,
    val startDayOfMonth: Int,
    val hideBalance: Boolean,
    val appLocked: Boolean,
    val driveMounted: Boolean,
    val importOldData: BackupImportState,
    val supportedLanguages: List<Language>,
    val currentLanguage: String
)