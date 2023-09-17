package com.ivy.core.features

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ivy.core.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Immutable
class BoolFeature(
    val name: String,
) {
    fun enabled(appContext: Context): Flow<Boolean?> {
        return appContext.dataStore.data.map {
            it[booleanPreferencesKey(name)]
        }
    }

    @Composable
    fun asState(defaultValue: Boolean = false): Boolean {
        val context = LocalContext.current
        val featureFlag = remember { enabled(context) }
            .collectAsState(defaultValue).value
        return featureFlag ?: defaultValue
    }

    suspend fun set(appContext: Context, enabled: Boolean) {
        appContext.dataStore.edit {
            it[booleanPreferencesKey(name)] = enabled
        }
    }
}