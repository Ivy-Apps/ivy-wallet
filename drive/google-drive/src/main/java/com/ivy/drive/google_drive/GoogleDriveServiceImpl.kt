package com.ivy.drive.google_drive


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Some
import arrow.core.computations.either
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.ivy.drive.google_drive.api.GoogleDriveService
import com.ivy.drive.google_drive.data.DriveMimeType
import com.ivy.drive.google_drive.data.GoogleDriveError
import com.ivy.drive.google_drive.data.GoogleDriveFileId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GoogleDriveServiceImpl @Inject constructor(
    private val googleDriveProvider: GoogleDriveProvider
) : GoogleDriveService, GoogleDriveProvider by googleDriveProvider {
    override fun isMounted(): Boolean = errorOrDrive.isRight()

    override suspend fun read(path: Path): Either<GoogleDriveError, ByteArray?> = either {
        fetchFileFromPath(path).bind()?.let { file ->
            fetchFileContentsById(GoogleDriveFileId(file.id)).bind()
        }
    }

    override suspend fun write(
        path: Path,
        content: ByteArray,
        mimeType: DriveMimeType
    ): Either<GoogleDriveError, Unit> = either {
        val maybeFile = fetchFileFromPath(path).bind()
        if (maybeFile == null) {
            createFileAndDirectoryStructure(path, content, mimeType).bind()
        } else {
            updateFile(
                ByteArrayContent(mimeType.value, content),
                GoogleDriveFileId(maybeFile.id)
            ).bind()

        }
    }

    override suspend fun delete(path: Path): Either<GoogleDriveError, Unit> = either {
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

    private suspend fun deleteFileById(fileId: GoogleDriveFileId): Either<GoogleDriveError, Void> =
        either {
            val drive = errorOrDrive.bind()
            Either.catch {
                withContext(Dispatchers.IO) {
                    drive.files().delete(fileId.id).execute()
                }
            }.mapLeft { GoogleDriveError.IOError(it) }.bind()
        }

    private suspend fun fetchFileContentsById(fileId: GoogleDriveFileId)
            : Either<GoogleDriveError, ByteArray> = either {
        val drive = errorOrDrive.bind()
        withContext(Dispatchers.IO) {
            Either.catch {
                drive.files().get(fileId.id).executeMediaAsInputStream().readBytes()
            }.mapLeft { GoogleDriveError.IOError(it) }
        }.bind()
    }

    private suspend fun updateFile(
        content: ByteArrayContent,
        fileId: GoogleDriveFileId
    ): Either<GoogleDriveError, File> = either {
        val drive = errorOrDrive.bind()
        Either.catch {
            withContext(Dispatchers.IO) {
                drive.files().update(fileId.id, null, content).execute()
            }
        }.mapLeft { GoogleDriveError.IOError(it) }.bind()
    }

    private suspend fun fetchFileFromPath(path: Path): Either<GoogleDriveError, File?> {
        suspend fun fetchFileFromPathHelper(
            pathAsList: List<String>,
            parentId: GoogleDriveFileId? = null
        ): Either<GoogleDriveError, File?> =
            either {
                if (pathAsList.size <= 1) {
                    val fileName = pathAsList.first()
                    fetchNode(fileName, parentId).bind()
                } else {
                    val directoryName = pathAsList.first()
                    val directory = fetchNode(directoryName, parentId).bind()
                        ?: return@either null // a required directory in the path is missing
                    // => the file is not found
                    fetchFileFromPathHelper(
                        pathAsList = pathAsList.drop(1),
                        parentId = directory.id?.let(::GoogleDriveFileId)
                    ).bind()
                }
            }
        return fetchFileFromPathHelper(path.toList().map { it.toString() })
    }


    private suspend fun fetchNode(
        fileName: String,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveError, File?> = either {
        val drive = errorOrDrive.bind()
        Either.catch({ GoogleDriveError.IOError(it) }) {
            val querySuffix = parentId?.let { " and '${it.id}' in parents" } ?: ""
            val query = "name = '$fileName'$querySuffix"
            queryFiles(drive, query)
        }.bind()?.firstOrNull()
    }

    private suspend fun queryFiles(
        drive: Drive,
        query: String
    ): List<File>? = withContext(Dispatchers.IO) {
        drive.files().list().apply {
            q = query
        }.execute()?.files
    }

    private suspend fun createFile(
        fileName: String,
        bytes: ByteArray,
        mimeType: DriveMimeType,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveError, File> = insertNode(
        nodeName = fileName,
        parentId = parentId,
        content = ByteArrayContent(mimeType.value, bytes)
    )


    private suspend fun insertNode(
        nodeName: String,
        directory: Boolean = false,
        parentId: GoogleDriveFileId? = null,
        content: ByteArrayContent? = null
    ): Either<GoogleDriveError, File> = either {
        val drive = errorOrDrive.bind()
        withContext(Dispatchers.IO) {
            val metadata = createMetadata(nodeName, directory, parentId)
            Either.catch {
                val files = drive.files()
                val create = if (content == null) {
                    // WARNING: the create() without media content must be used for folders
                    files.create(metadata)
                } else {
                    // This cannot create folders!! Only for files
                    files.create(metadata, content)
                }
                create.execute()
            }.mapLeft { GoogleDriveError.IOError(it) }
        }.bind()
    }

    private fun createMetadata(
        nodeName: String,
        directory: Boolean,
        parentId: GoogleDriveFileId?
    ): File = File().apply {
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
    ): Either<GoogleDriveError, GoogleDriveFileId> =
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