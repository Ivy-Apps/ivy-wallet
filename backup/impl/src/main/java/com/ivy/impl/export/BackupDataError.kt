package com.ivy.impl.export

sealed interface BackupDataError {
    val reason: Throwable?

    data class SaveBackupZipLocally(override val reason: Throwable?) : BackupDataError
    data class UploadToDrive(override val reason: Throwable?) : BackupDataError
}