package com.ivy.backup.base.data

import com.ivy.data.Theme

@Deprecated("will be removed!")
data class SettingsData(
    val baseCurrency: String,
    val theme: Theme,
)