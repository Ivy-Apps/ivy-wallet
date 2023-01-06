package com.ivy.drive.google_drive.util

import com.google.api.services.drive.model.File

object GoogleDriveUtil {
    const val DRIVE_SPACE = "drive"
    const val IVY_WALLET_ROOT_FOLDER = "IvyWallet"
    const val IVY_WALLET_BACKUP_FOLDER = "Backups"
    const val IVY_WALLET_BACKUP_FILENAME = "IvyWalletBackup"
    const val APP_NAME = "IvyAndroidApp"
    const val MIME_TYPE_FOLDER = "application/vnd.google-apps.folder"
    val ivyRootFolderParent = listOf("root")

    // TODO: Make this private
    fun createGoogleDriveFile(
        fileName: String,
        parents: List<String>,
        type: String
    ): File {
        return File()
            .setName(fileName)
            .setParents(parents)
            .setMimeType(type)
    }

    fun updateGoogleDriveFile(
        fileName: String,
        type: String
    ): File {
        return File()
            .setName(fileName)
            .setMimeType(type)
    }

    fun getParents(fileId: String): List<String> {
        return listOf(fileId)
    }
}