package com.ivy.drive.google_drive.data

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.ivy.drive.google_drive.data.GoogleDriveFileType.Image
import com.ivy.drive.google_drive.data.GoogleDriveFileType.Backup
import com.ivy.drive.google_drive.data.GoogleDriveFileType.Video
import com.ivy.drive.google_drive.util.GoogleDriveUtil
import com.ivy.drive.google_drive.util.GoogleDriveUtil.IVY_WALLET_ROOT_FOLDER
import com.ivy.drive.google_drive.util.GoogleDriveUtil.createGoogleDriveFile
import com.ivy.drive.google_drive.util.GoogleDriveUtil.getParents
import com.ivy.drive.google_drive.util.GoogleDriveUtil.ivyRootFolderParent
import com.ivy.drive.google_drive.util.GoogleDriveUtil.updateGoogleDriveFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

internal class GoogleDriveServiceHelper(private val drive: Drive) {

    fun uploadFileAsync(
        file: File,
        fileContent: FileContent? = null,
        driveFiletype: GoogleDriveFileType
    ): Deferred<String> {

        return CoroutineScope(Dispatchers.IO).async {
            val currentFile = getFileByName(file.name)

            if(currentFile != null) {
                Timber.d("Current File not null")
                val parentFolder = checkIfFolderExist(driveFiletype.toString())
                if (fileContent != null) {
                    updateFile(
                        fileId = currentFile.id,
                        driveType = driveFiletype,
                        fileName = currentFile.name,
                        parent = parentFolder,
                        fileContent = fileContent
                    )
                } else {
                    "Couldn't upload the file as contents were empty"
                }
            } else {
                Timber.d("Current File null creating new file")

                createFile(
                    fileContent = fileContent,
                    fileName = file.name,
                    driveType = driveFiletype
                )
            }
        }
    }

    fun downloadFile(fileName: String): OutputStream? {
        val file = getFileByName(fileName)
        val outputStream = ByteArrayOutputStream()
        if(file != null) {
            drive.files().get(file.id).executeAndDownloadTo(outputStream)
            return outputStream
        }
        return null
    }

    private fun updateFile(
        fileId: String,
        fileContent: FileContent,
        fileName: String,
        parent: String?,
        driveType: GoogleDriveFileType
    ): String {
        val type = driveType.toString()
        val fileMetadata = updateGoogleDriveFile(fileName,type)
        val googleFile = drive.files().update(fileId,fileMetadata,fileContent).setAddParents(parent).execute()
            ?: throw IOException("Error while updating and uploading google drive file")
        return googleFile.id
    }

    private fun createFile(
        fileContent: FileContent?,
        fileName: String,
        driveType: GoogleDriveFileType
    ): String {
        val type = when(driveType) {
            is Backup -> driveType.type
            is Image -> driveType.type
            is Video -> driveType.type
        }

        val typeValue = when(type) {
            is GoogleDriveType.BackupTypeCSV -> type.VALUE
            is GoogleDriveType.ImageTypeJPEG -> type.VALUE
            is GoogleDriveType.ImageTypePNG -> type.VALUE
            is GoogleDriveType.ImageTypeWEBP -> type.VALUE
            is GoogleDriveType.BackupTypePlainText -> type.VALUE
            else -> {
                GoogleDriveType.BackupTypePlainText.VALUE
            }
        }
        val parentId = checkIfFolderExist(driveType.toString())
        val parents = if(parentId != null) {
            getParents(parentId)
        } else {
            val rootId = checkIfFolderExist(IVY_WALLET_ROOT_FOLDER)
            val rootParents =
                if(rootId != null) {
                    getParents(rootId)
                } else {
                    val id = createFolder()
                    getParents(id)
                }
            val id = createFolder(rootParents,driveType.toString())
            getParents(id)
        }
        val fileMetadata = createGoogleDriveFile(fileName,parents,typeValue)

        val googleFile =
            if(fileContent == null) {
                drive.files().create(fileMetadata).execute()
                    ?: throw IOException("Error while creating and uploading google drive file")
            } else {
              drive.files().create(fileMetadata, fileContent).execute()
                    ?: throw IOException("Error while creating and uploading google drive file")
            }
        return googleFile.id
    }

    private fun getFileByName(fileName: String): File? {
        val fileList = getAllFiles()
        if(fileList.isEmpty().not()) {
            return fileList.files.firstOrNull { it.name == fileName }
        }
        return null
    }

    private fun getAllFiles(): FileList {
        return drive.files().list().setSpaces(GoogleDriveUtil.DRIVE_SPACE).execute()
            ?: throw Exception ("Error in querying the files from the drive")
    }

    private fun checkIfFolderExist(fileName: String): String? {
        val files = getAllFiles()
        val file = files.files.firstOrNull { it.name == fileName }
        return file?.id
    }

    private fun createFolder(
        parents: List<String> = ivyRootFolderParent,
        folderName: String = IVY_WALLET_ROOT_FOLDER
    ): String {
        val metadata = createGoogleDriveFile(
            folderName,
            parents,
            GoogleDriveUtil.MIME_TYPE_FOLDER
        )
        val googleFile = drive.files().create(metadata).execute()
            ?: throw IOException("Error when requesting file creation.")
        return googleFile.id
    }
}