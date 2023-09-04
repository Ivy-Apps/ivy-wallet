package com.ivy.wallet.backup.github.ui

sealed interface GitHubBackupStatus {
    object Loading : GitHubBackupStatus
    data class Error(val error: String) : GitHubBackupStatus
    data object Success : GitHubBackupStatus
}

data class GitHubBackupInput(
    val repoUrl: String,
    val gitHubPAT: String,
)

data class LastBackupInfo(
    val time: String,
    val indicateDanger: Boolean
)
