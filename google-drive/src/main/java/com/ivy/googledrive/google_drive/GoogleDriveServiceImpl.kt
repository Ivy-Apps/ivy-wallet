package com.ivy.googledrive.google_drive

import android.util.Log
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class GoogleDriveServiceImpl @Inject constructor(
    private val drive: Drive,
) : GoogleDriveService {

    override suspend fun createFile(
        file: File,
        fileName: String,
        parentFolders: List<String>?,
        mimeType: DriveMimeType,
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val metadataFile = com.google.api.services.drive.model.File().apply {
                    name = fileName
                    parentFolders?.let {
                        setParents(it)
                    }
                }
                val fileContent = FileContent(mimeType.value, file)
                drive.files()
                    .create(metadataFile, fileContent)
                    .setFields("id")
                    .execute().id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun createFolder(
        folderName: String,
        parentFolders: List<String>?
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val metadataFolder = com.google.api.services.drive.model.File().apply {
                    name = folderName
                    parentFolders?.let {
                        setParents(it)
                    }
                    mimeType = "application/vnd.google-apps.folder"
                }
                drive.files().create(metadataFolder).setFields("id").execute().id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun searchForFile(
        mimeType: DriveMimeType,
        name: String
    ): com.google.api.services.drive.model.File? {
        return withContext(Dispatchers.IO) {
            try {
                drive.files()
                    .list()
                    .setQ("mimeType='${mimeType.value}'")
                    .execute()
                    .files
                    .firstOrNull { it.name == name }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun searchForFiles(query: String): List<com.google.api.services.drive.model.File> {
        return withContext(Dispatchers.IO) {
            try {
                drive.files()
                    .list()
                    .setQ(query)
                    .execute()
                    .files
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }


    override suspend fun deleteFile(
        fileId: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                drive.files().delete(fileId).execute()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}