package com.ivy.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalLegalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getIsDisclaimerAccepted(): Boolean? = dataStore.data
        .map { it[DisclaimerAcceptedKey] }.firstOrNull()

    suspend fun setDisclaimerAccepted(accepted: Boolean) {
        dataStore.edit {
            it[DisclaimerAcceptedKey] = accepted
        }
    }

    companion object {
        private val DisclaimerAcceptedKey = booleanPreferencesKey("disclaimer_accepted")
    }
}