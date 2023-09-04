package com.ivy.wallet.backup.github

import android.content.Context
import androidx.datastore.preferences.core.edit
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.ivy.wallet.data.DatastoreKeys
import com.ivy.wallet.data.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GitHubCredentialsManager @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) {

    suspend fun getCredentials(): Either<String, GitHubCredentials> = withContext(Dispatchers.IO) {
        either {
            val data = appContext.dataStore.data.firstOrNull()
            ensureNotNull(data) {
                "Error: Datastore data is null!"
            }
            val token = data[DatastoreKeys.GITHUB_PAT]
            ensureNotNull(token) {
                "GitHub PAT (Personal Access Token) isn't configured."
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
                it[DatastoreKeys.GITHUB_PAT] = gitHubPAT
            }
        }
    }

    suspend fun removeSaved(): Unit = withContext(Dispatchers.IO) {
        appContext.dataStore.edit {
            it.remove(DatastoreKeys.GITHUB_OWNER)
            it.remove(DatastoreKeys.GITHUB_REPO)
            it.remove(DatastoreKeys.GITHUB_PAT)
        }
    }
}
