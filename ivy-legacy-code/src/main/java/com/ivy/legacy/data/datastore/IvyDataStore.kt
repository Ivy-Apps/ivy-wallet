package com.ivy.wallet.io.persistence.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("legacy - use IvyWalletDataStore instead")
@Singleton
class IvyDataStore @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    @Deprecated("legacy")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ivy_wallet")

    @Deprecated("legacy")
    suspend fun <T> insert(pair: Preferences.Pair<T>) {
        appContext.dataStore.edit {
            it.putAll(pair)
        }
    }

    @Deprecated("legacy")
    suspend fun <T> insert(
        key: Preferences.Key<T>,
        value: T
    ) {
        appContext.dataStore.edit {
            it[key] = value
        }
    }

    @Deprecated("legacy")
    suspend fun <T> remove(key: Preferences.Key<T>) {
        appContext.dataStore.edit {
            it.remove(key = key)
        }
    }

    @Deprecated("legacy")
    suspend fun <T> get(key: Preferences.Key<T>): T? = appContext.dataStore.data.map {
        it[key]
    }.firstOrNull()
}
