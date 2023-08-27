package com.ivy.wallet.backup.github.ui


sealed interface GitHubBackupStatus {
    object Loading : GitHubBackupStatus
    data class Error(val error: String) : GitHubBackupStatus
    data object Success : GitHubBackupStatus
}