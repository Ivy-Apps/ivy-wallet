package com.ivy.wallet.backup.github.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.backup.github.GitHubAutoBackupManager
import com.ivy.wallet.backup.github.GitHubBackup
import com.ivy.wallet.backup.github.GitHubCredentials
import com.ivy.wallet.backup.github.GitHubCredentialsManager
import com.ivy.wallet.datetime.toLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GitHubBackupViewModel @Inject constructor(
    private val gitHubBackup: GitHubBackup,
    private val navigation: Navigation,
    private val gitHubAutoBackupManager: GitHubAutoBackupManager,
    private val gitHubCredentialsManager: GitHubCredentialsManager,
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
                navigation.back()
                backupData()
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
}