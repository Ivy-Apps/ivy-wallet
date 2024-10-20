package com.ivy.domain.features

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ivy.data.datastore.DatastoreKeys
import com.ivy.data.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Immutable
class BoolFeature(
    val key: String,
    val group: FeatureGroup? = null,
    val name: String? = null,
    val description: String? = null,
    private val defaultValue: Boolean
) {
    @Composable
    fun asEnabledState(): Boolean {
        val context = LocalContext.current
        val featureFlag = remember { enabledFlow(context) }
            .collectAsState(defaultValue).value
        return featureFlag ?: defaultValue
    }

    suspend fun isEnabled(appContext: Context): Boolean =
        enabledFlow(appContext).first() ?: defaultValue

    fun enabledFlow(appContext: Context): Flow<Boolean?> = appContext.dataStore
        .data.map {
            it[featureKey] ?: defaultValue
        }

    suspend fun set(appContext: Context, enabled: Boolean) {
        appContext.dataStore.edit {
            it[featureKey] = enabled
        }
    }

    private val featureKey: Preferences.Key<Boolean>
        get() = DatastoreKeys.ivyFeature(key)
}
