package com.ivy.drive.google_drive.data

sealed interface GoogleDriveError {
    object NotMounted : GoogleDriveError
    data class IOError(val exception: Throwable) : GoogleDriveError
}