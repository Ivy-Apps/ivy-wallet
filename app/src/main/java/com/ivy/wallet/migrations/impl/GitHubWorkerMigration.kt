package com.ivy.wallet.migrations.impl

import com.ivy.wallet.backup.github.GitHubAutoBackupManager
import com.ivy.wallet.backup.github.GitHubBackup
import com.ivy.wallet.migrations.Migration
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject

class GitHubWorkerMigration @Inject constructor(
    private val gitHubBackup: GitHubBackup,
    private val gitHubAutoBackupManager: GitHubAutoBackupManager,
) : Migration {
    override val key: String
        get() = "github_auto_backup_worker_v2"

    override suspend fun migrate() {
        if (gitHubBackup.enabled.firstOrNull() == true) {
            gitHubAutoBackupManager.scheduleAutoBackups()
            Timber.i("GitHub auto-backups worker rescheduled.")
        }
    }
}