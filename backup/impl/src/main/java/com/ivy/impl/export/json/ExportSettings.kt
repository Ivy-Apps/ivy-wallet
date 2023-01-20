package com.ivy.impl.export.json

import androidx.datastore.preferences.core.Preferences
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject

internal suspend fun exportSettingsToJson(
    dataStore: IvyDataStore,
    settingsKeys: SettingsKeys
): JSONObject {
    suspend fun <T> JSONObject.add(
        key: Preferences.Key<T>,
    ) {
        dataStore.get(key).firstOrNull()?.let {
            put(key.name, it)
        }
    }

    val json = JSONObject()
    json.add(settingsKeys.baseCurrency)
    json.add(settingsKeys.theme)
    json.add(settingsKeys.startDayOfMonth)
    json.add(settingsKeys.displayName)
    json.add(settingsKeys.hideBalance)
    return json
}