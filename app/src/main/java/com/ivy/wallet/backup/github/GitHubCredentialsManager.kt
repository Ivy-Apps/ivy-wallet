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
            val token = encryptedSharedPrefs.get()
                .getString(EncryptedPrefsKeys.BACKUP_GITHUB_PAT, null)
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
                accessToken = token,
            )
        }
    }

    suspend fun saveCredentials(
        gitHubUrl: String,
        gitHubPAT: String,
    ): Either<String, Unit> = withContext(Dispatchers.IO) {
        either {
            val parsedUrl = parseGitHubUrl(gitHubUrl).bind()
            appContext.dataStore.edit {
                it[DatastoreKeys.GITHUB_OWNER] = parsedUrl.owner
                it[DatastoreKeys.GITHUB_REPO] = parsedUrl.repo
            }
            encryptedSharedPrefs.get().edit()
                .putString(EncryptedPrefsKeys.BACKUP_GITHUB_PAT, gitHubPAT)
                .apply()
        }
    }

    private fun parseGitHubUrl(url: String): Either<String, ParsedUrl> = either {
        // This regex handles optional 'https', optional 'www', and captures the owner and repo.
        val regex = """https?://(?:www\.)?github\.com/([^/]+)/([^/]+)""".toRegex()
        val matchResult = regex.find(url)

        val owner = matchResult?.groups?.get(1)?.value
        ensureNotNull(owner) {
            "Couldn't parse 'owner' from \"$url.\""
        }
        val repo = matchResult.groups?.get(2)?.value
        ensureNotNull(repo) {
            "Couldn't parse 'repo' from \"$url.\""
        }

        ParsedUrl(owner, repo)
    }

    private data class ParsedUrl(
        val owner: String,
        val repo: String,
    )
}

