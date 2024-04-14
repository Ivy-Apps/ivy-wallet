package com.ivy.data.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ivy_wallet_datastore_v1"
)

@Composable
fun datastore(): DataStore<Preferences> {
    return LocalContext.current.dataStore
}
