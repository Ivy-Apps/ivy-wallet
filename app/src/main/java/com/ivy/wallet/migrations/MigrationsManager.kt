package com.ivy.wallet.migrations

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ivy.data.datastore.dataStore
import com.ivy.wallet.migrations.impl.DisableGitHubAutoBackupMigration
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MigrationsManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val disableGitHubAutoBackupMigration: Lazy<DisableGitHubAutoBackupMigration>,
) {
    private val migrations by lazy {
        listOf(
            disableGitHubAutoBackupMigration.get(),
        )
    }

    suspend fun executeMigrations() {
        delay(2_000L) // to not the make the app start slower

        val data = context.dataStore.data.firstOrNull()

        for (migration in migrations) {
            val key = booleanPreferencesKey("migration_${migration.key}")
            val isExecuted = data?.get(key) ?: false
            if (!isExecuted) {
                Timber.i("[MIGRATION] Executing '${migration.key}' migration...")
                try {
                    withContext(Dispatchers.IO) {
                        migration.migrate()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Mark the migration as executed
                context.dataStore.edit {
                    it[key] = true
                }
                Timber.i("[MIGRATION] Finished '${migration.key}' migration.")
            }
        }
    }
}
