package com.ivy.core.persistence.datastore.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsKeys @Inject constructor() {
    val baseCurrency by lazy { stringPreferencesKey(name = "base_currency") }
    val startDayOfMonth by lazy { intPreferencesKey(name = "start_day_of_month") }
    val hideBalance by lazy { booleanPreferencesKey(name = "hide_balance") }
    val appLocked by lazy { booleanPreferencesKey(name = "app_locked") }
    val displayName by lazy { stringPreferencesKey(name = "display_name") }
    val theme by lazy { intPreferencesKey(name = "theme") }
}