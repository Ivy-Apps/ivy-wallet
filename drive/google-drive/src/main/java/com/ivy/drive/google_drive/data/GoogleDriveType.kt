package com.ivy.drive.google_drive.data

sealed interface GoogleDriveType {
    object BackupTypeCSV: GoogleDriveType {
        const val VALUE = "text/csv"
    }
    object BackupTypePlainText: GoogleDriveType {
        const val VALUE = "text/plain"
    }
    object ImageTypePNG: GoogleDriveType {
        const val VALUE = "image/png"
    }
    object ImageTypeJPEG: GoogleDriveType {
        const val VALUE = "image/jpeg"
    }
    object ImageTypeWEBP: GoogleDriveType {
        const val VALUE = "image/webp"
    }
    object VideoType: GoogleDriveType {
        const val VALUE = "video/*"
    }
}