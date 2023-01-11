package com.ivy.drive.google_drive.drivev2


import androidx.appcompat.app.AppCompatActivity
import arrow.core.*
import arrow.core.computations.either
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.ivy.drive.google_drive.MountDriveLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File as JavaIoFile

@Singleton
class GoogleDriveServiceImpl @Inject constructor(
    private val mountDriveLauncher: MountDriveLauncher
) : GoogleDriveService {
    private var mountedDrive: Drive? = null

    override fun wire(activity: AppCompatActivity) {
        mountDriveLauncher.wire(activity)
    }

    override fun mount() {
        mountDriveLauncher.launch(Unit) { drive ->
            this.mountedDrive = drive
        }
    }

    override fun isMounted(): Boolean = mountedDrive != null

    private fun mountedDrive(): Either<GoogleDriveError, Drive> =
        mountedDrive?.right() ?: GoogleDriveError.NotMounted.left()

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
            tempFileResource(content) {
                val fileContent = FileContent(mimeType.value, it)
                updateFile(fileContent, GoogleDriveFileId(maybeFile.id)).bind()
            }
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
            val drive = mountedDrive().bind()
            Either.catch {
                withContext(Dispatchers.IO) {
                    drive.files().delete(fileId.id).execute()
                }
            }.mapLeft { GoogleDriveError.IOError }.bind()
        }

    private suspend fun fetchFileContentsById(fileId: GoogleDriveFileId)
            : Either<GoogleDriveError, ByteArray> = either {
        val drive = mountedDrive().bind()
        withContext(Dispatchers.IO) {
            Either.catch {
                drive.files().get(fileId.id).executeMediaAsInputStream().readBytes()
            }.mapLeft { GoogleDriveError.IOError }
        }.bind()
    }

    private suspend fun updateFile(
        fileContent: FileContent,
        fileId: GoogleDriveFileId
    ): Either<GoogleDriveError, File> = either {
        val drive = mountedDrive().bind()
        Either.catch {
            withContext(Dispatchers.IO) {
                drive.files().update(fileId.id, null, fileContent).execute()
            }
        }.mapLeft { GoogleDriveError.IOError }.bind()
    }

    private suspend fun fetchFileFromPath(path: Path): Either<GoogleDriveError, File?> {
        suspend fun fetchFileFromPathHelper(
            pathAsStringList: List<String>,
            parentId: GoogleDriveFileId? = null
        ): Either<GoogleDriveError, File?> =
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
    ): Either<GoogleDriveError, File?> = either {
        val drive = mountedDrive().bind()
        Either.catch({ GoogleDriveError.IOError }) {
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


    // TODO: 08-Jan-23 figure out a better way to turn a ByteArray into a File...
    private suspend fun <T> tempFileResource(
        bytes: ByteArray,
        functionToExecute: suspend (JavaIoFile) -> T
    ): T {
        val tempFile = withContext(Dispatchers.IO) {
            JavaIoFile.createTempFile("temporary", "file").apply {
                writeBytes(bytes)
            }
        }
        return functionToExecute(tempFile).also {
            tempFile.delete()
        }
    }

    private suspend fun createFile(
        fileName: String,
        bytes: ByteArray,
        mimeType: DriveMimeType,
        parentId: GoogleDriveFileId? = null
    ): Either<GoogleDriveError, File> =
        tempFileResource(bytes) {
            val fileContent = FileContent(mimeType.value, it)
            insertNode(nodeName = fileName, parentId = parentId, fileContent = fileContent)
        }

    private suspend fun insertNode(
        nodeName: String,
        directory: Boolean = false,
        parentId: GoogleDriveFileId? = null,
        fileContent: FileContent? = null
    ): Either<GoogleDriveError, File> = either {
        val drive = mountedDrive().bind()
        withContext(Dispatchers.IO) {
            val metadata = createMetadata(nodeName, directory, parentId)
            Either.catch {
                drive.files()
                    .create(metadata, fileContent)
                    .execute()
            }.mapLeft { GoogleDriveError.IOError }
        }.bind()
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