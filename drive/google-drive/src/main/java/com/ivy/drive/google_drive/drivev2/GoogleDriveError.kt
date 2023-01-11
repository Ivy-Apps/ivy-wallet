package com.ivy.drive.google_drive.drivev2

sealed interface GoogleDriveError {
    object NotMounted : GoogleDriveError
    object IOError : GoogleDriveError
}