package com.ivy.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DatastoreKeys {
    @Deprecated("will be removed")
    val GITHUB_OWNER = stringPreferencesKey("github_backup_owner")

    @Deprecated("will be removed")
    val GITHUB_REPO = stringPreferencesKey("github_backup_repo")

    @Deprecated("will be removed")
    val GITHUB_PAT = stringPreferencesKey("github_backup_pat")

    @Deprecated("will be removed")
    val GITHUB_LAST_BACKUP_EPOCH_SEC =
        longPreferencesKey("github_backup_last_backup_time_epoch_sec")

    fun ivyFeature(key: String): Preferences.Key<Boolean> {
        return booleanPreferencesKey("feature_$key")
    }
}