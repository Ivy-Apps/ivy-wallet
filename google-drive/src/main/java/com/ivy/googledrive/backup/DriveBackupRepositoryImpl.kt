package com.ivy.googledrive.backup

import android.util.Log
import androidx.work.WorkManager
import com.ivy.googledrive.backup.DriveBackupConstants.IVY_FOLDER_NAME
import com.ivy.googledrive.google_auth.GoogleAuthService
import com.ivy.googledrive.google_drive.DriveMimeType
import com.ivy.googledrive.google_drive.GoogleDriveService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DriveBackupRepositoryImpl(private val googleDriveService: GoogleDriveService) :
    DriveBackupRepository {
    override suspend fun backupFile(
        file: File,
        fileName: String,
        mimeType: DriveMimeType,
        n: Int
    ): String? {
        val ivyFolder = getOrCreateIvyFolder()
        val backedUpFiles = getBackedUpFiles(ivyFolder)
        if (backedUpFiles.size >= n) { // The file's capacity is full
            val filesToBeDeleted = backedUpFiles.subList(n - 1, backedUpFiles.size)
            val deletionResult = deleteFiles(filesToBeDeleted.map { it.id })
            if (!deletionResult) {
                return null
            }
        }
        return googleDriveService.createFile(
            file = file,
            fileName = fileName,
            parentFolders = listOf(ivyFolder),
            mimeType = mimeType
        )
    }

    // Note : If the user puts the Ivy folder in the trash (on google drive), this function will consider that the file is still existed
    // And it won't create another one
    private suspend fun getOrCreateIvyFolder(): String {
        val folder = googleDriveService.searchForFile(DriveMimeType.DRIVE_FOLDER, IVY_FOLDER_NAME)
        return if (folder == null) {
            createFolder(IVY_FOLDER_NAME)
        } else {
            folder.id
        }
    }

    private suspend fun createFolder(
        name: String,
    ): String {
        return googleDriveService.createFolder(name)
            ?: throw Exception("Cannot create a folder")
    }

    private suspend fun getBackedUpFiles(parentFileId: String): List<com.google.api.services.drive.model.File> {
        val backupFiles = googleDriveService.searchForFiles("'$parentFileId' in parents")
        return backupFiles
    }

    private suspend fun deleteFiles(files: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            val listOfDeletedFiles = mutableListOf<Boolean>()
            files.forEach { fileId ->
                listOfDeletedFiles.add(
                    listOfDeletedFiles.add(googleDriveService.deleteFile(fileId))
                )
            }
            listOfDeletedFiles.all { it }
        }
    }
}