package com.ivy.wallet.backup.github.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.backup.github.GitHubAutoBackupManager
import com.ivy.wallet.backup.github.GitHubBackup
import com.ivy.wallet.backup.github.GitHubCredentials
import com.ivy.wallet.backup.github.GitHubCredentialsManager
import com.ivy.wallet.datetime.toLocal
import com.ivy.wallet.domain.deprecated.logic.zip.BackupLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GitHubBackupViewModel @Inject constructor(
    private val gitHubBackup: GitHubBackup,
    private val gitHubAutoBackupManager: GitHubAutoBackupManager,
    private val gitHubCredentialsManager: GitHubCredentialsManager,
    @ApplicationContext
    private val context: Context,
    private val backupLogic: BackupLogic,
) : ViewModel() {

    val enabled = gitHubBackup.enabled

    val lastBackupTime: Flow<String?> = gitHubBackup.lastBackupTime.map { instant ->
        instant?.toLocal()?.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))
    }

    val backupStatus = MutableStateFlow<GitHubBackupStatus?>(null)

    fun backupData() {
        viewModelScope.launch {
            backupStatus.value = GitHubBackupStatus.Loading
            backupStatus.value = gitHubBackup.backupData().fold(
                ifLeft = { GitHubBackupStatus.Error(it.humanReadable) },
                ifRight = { GitHubBackupStatus.Success }
            )
            if (backupStatus.value == GitHubBackupStatus.Success) {
                delay(2_000L)
                backupStatus.value = null
            }
        }
    }

    fun enableBackups(
        gitHubUrl: String,
        gitHubPAT: String,
    ) {
        viewModelScope.launch {
            gitHubBackup.enable(
                gitHubUrl = gitHubUrl.trim(),
                gitHubPAT = gitHubPAT.trim(),
            ).onRight {
                gitHubAutoBackupManager.scheduleAutoBackups()
            }
        }
    }

    fun disableBackups() {
        viewModelScope.launch {
            gitHubBackup.disable()
            gitHubAutoBackupManager.cancelAutoBackups()
        }
    }

    fun viewBackup(onOpenUrl: (String) -> Unit) {
        viewModelScope.launch {
            gitHubCredentialsManager.getCredentials().onRight {
                onOpenUrl(it.toRepoUrl())
            }
        }
    }

    suspend fun getCredentials(): GitHubBackupInput? {
        return gitHubCredentialsManager.getCredentials().getOrNull()
            ?.let {
                GitHubBackupInput(
                    repoUrl = it.toRepoUrl(),
                    gitHubPAT = it.gitHubPAT
                )
            }
    }

    private fun GitHubCredentials.toRepoUrl(): String {
        return "https://github.com/${owner}/${repo}"
    }

    private var backupImportInProgress = false

    fun importFromGitHub() {
        viewModelScope.launch {
            showToast("Importing backup... Be patient!")
            if (backupImportInProgress) return@launch

            backupImportInProgress = true
            gitHubBackup.readBackupJson()
                .onRight { json ->
                    val result = backupLogic.importJson(json)
                    val toast = if (result.transactionsImported > 0) {
                        "Success! Imported ${result.transactionsImported} transactions."
                    } else {
                        "Import failed :/"
                    }
                    showToast(toast)
                    backupImportInProgress = false
                }
                .onLeft {
                    showToast(it)
                    backupImportInProgress = false
                }
        }
    }

    private fun showToast(
        text: String,
        duration: Int = Toast.LENGTH_LONG,
    ) {
        Toast.makeText(
            context,
            text,
            duration
        ).show()
    }

}