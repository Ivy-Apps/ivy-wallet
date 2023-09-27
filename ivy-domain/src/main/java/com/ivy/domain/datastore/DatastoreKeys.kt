package com.ivy.domain.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DatastoreKeys {
    val GITHUB_OWNER = stringPreferencesKey("github_backup_owner")
    val GITHUB_REPO = stringPreferencesKey("github_backup_repo")
    val GITHUB_PAT = stringPreferencesKey("github_backup_pat")
    val GITHUB_LAST_BACKUP_EPOCH_SEC =
        longPreferencesKey("github_backup_last_backup_time_epoch_sec")
}