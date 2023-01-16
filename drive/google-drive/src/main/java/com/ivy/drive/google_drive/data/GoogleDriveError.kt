package com.ivy.drive.google_drive.data

sealed interface GoogleDriveError {

    val exception: Throwable

    data class NotMounted(override val exception: Throwable) : GoogleDriveError
    data class IOError(override val exception: Throwable) : GoogleDriveError
}