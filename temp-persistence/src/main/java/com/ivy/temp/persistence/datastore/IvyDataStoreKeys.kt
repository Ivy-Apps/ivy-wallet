package com.ivy.temp.persistence.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyDataStoreKeys @Inject constructor() {
    val startDayOfMonth by lazy { intPreferencesKey(name = "start_day_of_month") }
    val hideBalance by lazy { booleanPreferencesKey(name = "hide_balance") }
}