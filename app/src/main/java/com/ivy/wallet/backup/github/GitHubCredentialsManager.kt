package com.ivy.wallet.backup.github

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.ivy.wallet.data.DatastoreKeys
import com.ivy.wallet.data.EncryptedPrefsKeys
import com.ivy.wallet.data.EncryptedSharedPrefs
import com.ivy.wallet.data.dataStore
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GitHubCredentialsManager @Inject constructor(
    @EncryptedSharedPrefs
    private val encryptedSharedPrefs: Lazy<SharedPreferences>,
    @ApplicationContext
    private val appContext: Context,
) {

    suspend fun getCredentials(): Either<String, GitHubCredentials> = withContext(Dispatchers.IO) {
        either {
            val token = getGitHubPAT()
            ensureNotNull(token) {
                "GitHub PAT (Personal Access Token) isn't configured."
            }
            val data = appContext.dataStore.data.firstOrNull()
            ensureNotNull(data) {
                "Error: Datastore data is null!"
            }
            val owner = data[DatastoreKeys.GITHUB_OWNER]
            ensureNotNull(owner) {
                "GitHub owner isn't configured."
            }
            val repo = data[DatastoreKeys.GITHUB_REPO]
            ensureNotNull(repo) {
                "GitHub repo isn't configured."
            }

            GitHubCredentials(
                owner = owner,
                repo = repo,
                gitHubPAT = token,
            )
        }
    }

    suspend fun saveCredentials(
        owner: String,
        repo: String,
        gitHubPAT: String,
    ): Either<String, Unit> = withContext(Dispatchers.IO) {
        either {
            appContext.dataStore.edit {
                it[DatastoreKeys.GITHUB_OWNER] = owner
                it[DatastoreKeys.GITHUB_REPO] = repo
            }
            encryptedSharedPrefs.get().edit()
                .putString(EncryptedPrefsKeys.BACKUP_GITHUB_PAT, gitHubPAT)
                .apply()
        }
    }

    suspend fun removeSaved(): Unit = withContext(Dispatchers.IO) {
        encryptedSharedPrefs.get().edit().remove(EncryptedPrefsKeys.BACKUP_GITHUB_PAT).apply()
        appContext.dataStore.edit {
            it.remove(DatastoreKeys.GITHUB_OWNER)
            it.remove(DatastoreKeys.GITHUB_REPO)
        }
    }


    private fun getGitHubPAT(): String? {
        return encryptedSharedPrefs.get()
            .getString(EncryptedPrefsKeys.BACKUP_GITHUB_PAT, null)
    }

    private data class ParsedUrl(
        val owner: String,
        val repo: String,
    )
}

