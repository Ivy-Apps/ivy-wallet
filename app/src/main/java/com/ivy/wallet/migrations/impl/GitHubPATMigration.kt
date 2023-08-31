package com.ivy.wallet.migrations.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import com.ivy.wallet.backup.github.GitHubBackup
import com.ivy.wallet.data.DatastoreKeys
import com.ivy.wallet.data.EncryptedPrefsKeys
import com.ivy.wallet.data.EncryptedSharedPrefs
import com.ivy.wallet.data.dataStore
import com.ivy.wallet.migrations.Migration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GitHubPATMigration @Inject constructor(
    @EncryptedSharedPrefs
    private val encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext
    private val context: Context,
    private val gitHubBackup: GitHubBackup
) : Migration {
    override val key: String
        get() = "github_pat_v1"

    override suspend fun migrate() {
        val gitHubPAT = encryptedSharedPreferences.getString(
            EncryptedPrefsKeys.BACKUP_GITHUB_PAT,
            null
        )

        if (gitHubPAT != null) {
            context.dataStore.edit {
                it[DatastoreKeys.GITHUB_PAT] = gitHubPAT
            }
            gitHubBackup.backupData(
                commitMsg = "[MIGRATION] Ivy Wallet data backup"
            )
        }
    }
}