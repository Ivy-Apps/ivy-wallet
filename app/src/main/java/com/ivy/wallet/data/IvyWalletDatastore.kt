package com.ivy.wallet.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ivy_wallet_datastore_v1"
)

object DatastoreKeys {
    val GITHUB_OWNER = stringPreferencesKey("github_backup_owner")
    val GITHUB_REPO = stringPreferencesKey("github_backup_repo")
}