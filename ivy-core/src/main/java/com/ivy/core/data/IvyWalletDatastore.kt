package com.ivy.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ivy.core.data.model.NumpadType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ivy_wallet_datastore_v1"
)

object DatastoreKeys {
    val GITHUB_OWNER = stringPreferencesKey("github_backup_owner")
    val GITHUB_REPO = stringPreferencesKey("github_backup_repo")
    val GITHUB_PAT = stringPreferencesKey("github_backup_pat")
    val GITHUB_LAST_BACKUP_EPOCH_SEC =
        longPreferencesKey("github_backup_last_backup_time_epoch_sec")
    internal val NUMPAD_TYPE = stringPreferencesKey("numpad_type")
}

// region numpad type
fun Preferences.getNumpadType(): NumpadType {
    val value = this[DatastoreKeys.NUMPAD_TYPE]
    return if (value == null) NumpadType.Calc else NumpadType.valueOf(value)
}

fun MutablePreferences.setNumpadType(value: NumpadType) {
    this[DatastoreKeys.NUMPAD_TYPE] = value.name
}

val Flow<Preferences>.numpadType: Flow<NumpadType>
    get() = this.map(Preferences::getNumpadType)
// endregion
