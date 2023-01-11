package com.ivy.drive.google_drive.drivev2

import androidx.appcompat.app.AppCompatActivity
import arrow.core.Either
import arrow.core.computations.either
import kotlinx.coroutines.flow.StateFlow
import java.nio.file.Path

interface GoogleDriveService {
    /**
     * Call in Activity's onCreate to register ActivityResultLauncher
     */
    fun wire(activity: AppCompatActivity)

    fun mount()

    // TODO: Instead of boolean return Option<DriveInfo> which
    //  contains the email of the mounted drive so it can be displayed in the UI
    val driveMounted: StateFlow<Boolean>

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


