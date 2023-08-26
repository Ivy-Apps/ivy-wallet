package com.ivy.wallet.backup.github.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.backup.github.GitHubBackup
import com.ivy.wallet.datetime.format
import com.ivy.wallet.datetime.toLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class GitHubBackupViewModel @Inject constructor(
    private val gitHubBackup: GitHubBackup,
    private val navigation: Navigation,
) : ViewModel() {

    val enabled = gitHubBackup.enabled

    val lastBackupTime: Flow<String?> = gitHubBackup.lastBackupTime.map { instant ->
        instant?.toLocal()?.format(FormatStyle.MEDIUM)
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
                navigation.back()
            }
        }
    }

    fun disableBackups() {
        viewModelScope.launch {
            gitHubBackup.disable()
        }
    }

    fun viewBackup(onOpenUrl: (String) -> Unit) {
        viewModelScope.launch {
            gitHubBackup.repoUrl()?.let {
                onOpenUrl(it)
            }
        }
    }
}

sealed interface GitHubBackupStatus {
    object Loading : GitHubBackupStatus
    data class Error(val error: String) : GitHubBackupStatus
    data object Success : GitHubBackupStatus
}