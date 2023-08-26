package com.ivy.wallet.backup.github

import android.content.Context
import androidx.datastore.preferences.core.edit
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.ivy.wallet.data.DatastoreKeys
import com.ivy.wallet.data.dataStore
import com.ivy.wallet.domain.deprecated.logic.zip.ExportBackupLogic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class GitHubBackup @Inject constructor(
    private val client: GitHubClient,
    private val credentialsManager: GitHubCredentialsManager,
    private val exportBackupLogic: ExportBackupLogic,
    @ApplicationContext
    private val appContext: Context
) {
    private val enabledInternalState = MutableStateFlow<Boolean?>(null)
    private val lastBackupTimeState = MutableStateFlow<Long?>(null)

    val enabled: Flow<Boolean> = enabledInternalState.map { state ->
        state ?: credentialsManager.getCredentials().isRight().also {
            enabledInternalState.value = it
        }
    }.distinctUntilChanged()

    val lastBackupTime: Flow<Instant?> = lastBackupTimeState.map { state ->
        val epochSeconds = state ?: appContext.dataStore.data
            .firstOrNull()?.let { it[DatastoreKeys.GITHUB_LAST_BACKUP_EPOCH_SEC] }

        epochSeconds?.let(Instant::ofEpochSecond)
    }

    suspend fun enable(
        gitHubUrl: String,
        gitHubPAT: String,
    ): Either<String, Unit> = either {
        val regex = """https?://(?:www\.)?github\.com/([^/]+)/([^/]+)""".toRegex()
        val matchResult = regex.find(gitHubUrl)

        val owner = matchResult?.groups?.get(1)?.value
        ensureNotNull(owner) {
            "Couldn't parse 'owner' from \"$gitHubUrl.\""
        }
        val repo = matchResult.groups[2]?.value
        ensureNotNull(repo) {
            "Couldn't parse 'repo' from \"$gitHubUrl.\""
        }

        credentialsManager.saveCredentials(
            owner = owner,
            repo = repo,
            gitHubPAT = gitHubPAT,
        ).bind()
        enabledInternalState.value = true
    }

    suspend fun disable() {
        credentialsManager.removeSaved()
        enabledInternalState.value = false
    }

    suspend fun repoUrl(): String? {
        return credentialsManager.getCredentials()
            .map { "https://github.com/${it.owner}/${it.repo}" }
            .getOrNull()
    }

    suspend fun backupData(): Either<Error, Unit> = withContext(Dispatchers.IO) {
        either {
            val credentials = credentialsManager.getCredentials()
                .mapLeft(Error::MissingCredentials).bind()

            val json = exportBackupLogic.generateJsonBackup()
            client.commit(
                credentials = credentials,
                path = "Ivy-Wallet-backup.json",
                content = json,
            ).mapLeft(Error::Commit).bind()

            appContext.dataStore.edit {
                val epochSecondsNow = Instant.now().epochSecond
                it[DatastoreKeys.GITHUB_LAST_BACKUP_EPOCH_SEC] = epochSecondsNow
                lastBackupTimeState.value = epochSecondsNow
            }
        }
    }

    sealed interface Error {
        val humanReadable: String

        data class MissingCredentials(val error: String) : Error {
            override val humanReadable: String
                get() = "Missing credentials: $error."
        }

        data class Commit(val error: String) : Error {
            override val humanReadable: String
                get() = "Failed to commit: $error."
        }
    }
}