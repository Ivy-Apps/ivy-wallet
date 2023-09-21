package com.ivy.core.features

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ivy.core.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Immutable
class BoolFeature(
    val key: String,
    val name: String? = null,
    val description: String? = null,
) {
    fun enabled(appContext: Context): Flow<Boolean?> {
        return appContext.dataStore.data.map {
            it[featureKey]
        }
    }

    @Composable
    fun asEnabledState(): Boolean {
        val context = LocalContext.current
        val featureFlag = remember { enabled(context) }
            .collectAsState(false).value
        return featureFlag ?: false
    }

    suspend fun set(appContext: Context, enabled: Boolean) {
        appContext.dataStore.edit {
            it[featureKey] = enabled
        }
    }

    private val featureKey: Preferences.Key<Boolean>
        get() = booleanPreferencesKey("feature_$key")
}