package com.ivy.drive.google_drive.drivev2

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Some
import arrow.core.computations.either
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

import java.io.File as JavaIoFile

class GoogleDriveServiceImpl(private val drive: Drive) : GoogleDriveService {
    override suspend fun read(path: Path): Either<GoogleDriveSDKError, ByteArray?> = either {
        fetchFileFromPath(path).bind()?.let { file ->
            fetchFileContentsById(GoogleDriveFileId(file.id)).bind()
        }

    }

    override suspend fun write(
        path: Path,
        content: ByteArray,
        mimeType: DriveMimeType
    ): Either<GoogleDriveSDKError, Unit> = either {
        val maybeFile = fetchFileFromPath(path).bind()
        if (maybeFile == null) {
            createFileAndDirectoryStructure(path, content, mimeType).bind()
        } else {
            tempFileResource(content) {
                val fileContent = FileContent(mimeType.value, it)
                updateFile(fileContent, GoogleDriveFileId(maybeFile.id)).bind()
            }
        }
    }

    override suspend fun delete(path: Path): Either<GoogleDriveSDKError, Unit> = either {
        fetchFileFromPath(path).bind()?.let { file ->
            deleteFileById(GoogleDriveFileId(file.id)).bind()
        }
    }

    private suspend fun createFileAndDirectoryStructure(
        path: Path,
        content: ByteArray,
        mimeType: DriveMimeType
    ) = either {
        val fileName = path.last().toString()
        val directories = path.toList().dropLast(1)
        when (val directoryList = NonEmptyList.fromList(directories)) {
            None -> createFile(fileName, content, mimeType).bind()
            is Some -> {
                val directoryId = createDirectoryTree(directoryList.value).bind()
                createFile(fileName, content, mimeType, directoryId).bind()
            }
        }
    }

    private suspend fun deleteFileById(fileId: GoogleDriveFileId) = Either.catch {
        withContext(Dispatchers.IO) {
            drive.files().delete(fileId.id).execute()
        }
    }.mapLeft { GoogleDriveSDKError.IOError }

    private suspend fun fetchFileContentsById(fileId: GoogleDriveFileId): Either<GoogleDriveSDKError, ByteArray> =
        withContext(Dispatchers.IO) {
            Either.catch {
                drive.files().get(fileId.id).executeMediaAsInputStream().readBytes()
            }.mapLeft { GoogleDriveSDKError.IOError }
        }

    private suspend fun updateFile(
        fileContent: FileContent,
        fileId: GoogleDriveFileId
    ): Either<GoogleDriveSDKError, File> = Either.catch {
        withContext(Dispatchers.IO) {
            drive.files().update(fileId.id, null, fileContent).execute()
        }
    }.mapLeft { GoogleDriveSDKError.IOError }

    private suspend fun fetchFileFromPath(path: Path): Either<GoogleDriveSDKError, File?> {
        suspend fun fetchFileFromPathHelper(
            pathAsStringList: List<String>,
            parentId: GoogleDriveFileId? = null
        ): Either<GoogleDriveSDKError, File?> =
            either {
                if (pathAsStringList.size <= 1) {
                    val fileName = pathAsStringList.first()
                    fetchNode(fileName, parentId).bind()
                } else {
                    val directoryName = pathAsStringList.first()
                    val directoryId =
                        fetchNode(directoryName, parentId).bind()?.id?.let { GoogleDriveFileId(it) }
                    fetchFileFromPathHelper(pathAsStringList.drop(1), directoryId).bind()
                }
            }
        return fetchFileFromPathHelper(path.toList().map { it.toString() })
    }


    private suspend fun fetchNode(
        fileName: String,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveSDKError, File?> = either {
        Either.catch({ GoogleDriveSDKError.IOError }) {
            val querySuffix = parentId?.let { " and '${it.id}' in parents" } ?: ""
            val query = "name = '$fileName'$querySuffix"
            queryFiles(query)
        }.bind()?.firstOrNull()
    }

    private suspend fun queryFiles(query: String): List<File>? = withContext(Dispatchers.IO) {
        drive.files().list().apply {
            q = query
        }.execute()?.files
    }

    // TODO: 08-Jan-23 figure out a better way to turn a ByteArray into a File...
    private suspend fun <T> tempFileResource(
        bytes: ByteArray,
        functionToExecute: suspend (JavaIoFile) -> T
    ): T {
        val tempFile = JavaIoFile.createTempFile("temporary", "file")
            .apply { writeBytes(bytes) }
        return functionToExecute(tempFile).also {
            tempFile.delete()
        }

    }

    private suspend fun createFile(
        fileName: String,
        bytes: ByteArray,
        mimeType: DriveMimeType,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveSDKError, File> =
        tempFileResource(bytes) {
            val fileContent = FileContent(mimeType.value, it)
            insertNode(nodeName = fileName, parentId = parentId, fileContent = fileContent)
        }

    private suspend fun insertNode(
        nodeName: String,
        directory: Boolean = false,
        parentId: GoogleDriveFileId? = null,
        fileContent: FileContent? = null
    ): Either<GoogleDriveSDKError, File> =
        withContext(Dispatchers.IO) {
            val metadata = createMetadata(nodeName, directory, parentId)
            Either.catch {
                drive.files()
                    .create(metadata, fileContent)
                    .execute()
            }.mapLeft { GoogleDriveSDKError.IOError }
        }

    private fun createMetadata(
        nodeName: String,
        directory: Boolean,
        parentId: GoogleDriveFileId?
    ): File = File()
        .apply {
            name = nodeName
            if (directory) {
                mimeType = DriveMimeType.FOLDER.value
            }
            if (parentId != null) {
                parents = listOf(parentId.id)
            }
        }

    private suspend fun createDirectoryTree(
        directories: NonEmptyList<Path>,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveSDKError, GoogleDriveFileId> =
        either {
            val name = directories.head.toString()
            val node = fetchNode(name, parentId).bind() ?: insertNode(
                nodeName = name,
                directory = true,
                parentId = parentId
            ).bind()
            val nextParentId = GoogleDriveFileId(node.id)
            val remainingDirectories = directories.drop(1)
            when (val nelRemainingDirectories = NonEmptyList.fromList(remainingDirectories)) {
                is Some -> createDirectoryTree(nelRemainingDirectories.value, nextParentId).bind()
                None -> nextParentId
            }
        }

}