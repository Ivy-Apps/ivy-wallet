package com.ivy.wallet.migrations.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.work.WorkManager
import com.ivy.data.datastore.DatastoreKeys
import com.ivy.data.datastore.dataStore
import com.ivy.wallet.migrations.Migration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DisableGitHubAutoBackupMigration @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : Migration {
    override val key: String
        get() = "disable_github_auto_backups"

    override suspend fun migrate() {
        // Cancel the GitHub worker
        WorkManager.getInstance(context).cancelUniqueWork("GITHUB_AUTO_BACKUP_WORK")

        context.dataStore.edit {
            it.remove(DatastoreKeys.GITHUB_PAT)
            it.remove(DatastoreKeys.GITHUB_REPO)
            it.remove(DatastoreKeys.GITHUB_OWNER)
            it.remove(DatastoreKeys.GITHUB_LAST_BACKUP_EPOCH_SEC)
        }
    }
}