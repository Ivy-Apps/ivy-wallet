package com.ivy.googledrive.google_drive

import androidx.work.WorkManager
import java.io.File

interface GoogleDriveService {

    suspend fun createFile(
        file: File,
        fileName: String,
        parentFolders: List<String>?,
        mimeType: DriveMimeType,
    ): String?

    suspend fun createFolder(
        folderName: String,
        parentFolders: List<String>? = null
    ): String?

    suspend fun searchForFile(
        mimeType: DriveMimeType,
        name: String
    ): com.google.api.services.drive.model.File?

    suspend fun searchForFiles(
        query: String
    ): List<com.google.api.services.drive.model.File>

    suspend fun deleteFile(
        fileId: String
    ): Boolean

}