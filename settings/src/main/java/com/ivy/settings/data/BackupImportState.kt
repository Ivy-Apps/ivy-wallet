package com.ivy.settings.data

sealed interface BackupImportState {
    object Idle : BackupImportState
    object Importing : BackupImportState
    data class Success(val message: String) : BackupImportState
    data class Error(val message: String) : BackupImportState
}