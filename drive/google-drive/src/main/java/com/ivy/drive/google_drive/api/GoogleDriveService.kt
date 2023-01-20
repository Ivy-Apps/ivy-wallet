package com.ivy.drive.google_drive.api

import arrow.core.Either
import arrow.core.computations.either
import com.ivy.drive.google_drive.data.DriveMimeType
import com.ivy.drive.google_drive.data.GoogleDriveError
import java.nio.file.Path

interface GoogleDriveService {
    fun isMounted(): Boolean

    suspend fun read(path: Path): Either<GoogleDriveError, ByteArray?>

    suspend fun readAsString(myPath: Path): Either<GoogleDriveError, String?> = either {
        read(myPath).bind()?.decodeToString()
    }

    suspend fun write(
        path: Path,
        content: ByteArray,
        mimeType: DriveMimeType
    ): Either<GoogleDriveError, Unit>

    suspend fun write(path: Path, content: String): Either<GoogleDriveError, Unit> =
        write(path, content.toByteArray(), DriveMimeType.TXT)

    suspend fun delete(path: Path): Either<GoogleDriveError, Unit>
}


