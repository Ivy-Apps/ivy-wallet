package com.ivy.drive.google_drive.drivev2

import arrow.core.Either
import arrow.core.computations.either
import java.nio.file.Path

interface GoogleDriveService {
    suspend fun read(path: Path): Either<GoogleDriveSDKError, ByteArray?>

    suspend fun readAsString(myPath: Path): Either<GoogleDriveSDKError, String?> = either {
        read(myPath).bind()?.decodeToString()
    }

    suspend fun write(
        path: Path,
        content: ByteArray,
        mimeType: DriveMimeType
    ): Either<GoogleDriveSDKError, Unit>

    suspend fun write(path: Path, content: String): Either<GoogleDriveSDKError, Unit> =
        write(path, content.toByteArray(), DriveMimeType.TXT)

    suspend fun delete(path: Path): Either<GoogleDriveSDKError, Unit>
}


@JvmInline
value class GoogleDriveFileId(val id: String)

sealed interface GoogleDriveSDKError {
    object IOError : GoogleDriveSDKError
}

enum class DriveMimeType(val value: String) {
    PDF("application/pdf"),
    TXT("text/plain"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    CSV("text/csv"),
    ZIP("application/zip"),
    FOLDER("application/vnd.google-apps.folder"),
}
