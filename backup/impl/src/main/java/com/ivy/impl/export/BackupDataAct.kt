package com.ivy.impl.export

import android.content.Context
import android.net.Uri
import arrow.core.Either
import arrow.core.computations.either
import com.ivy.core.domain.action.Action
import com.ivy.drive.google_drive.api.GoogleDriveService
import com.ivy.drive.google_drive.data.DriveMimeType
import com.ivy.file.readFileAsBytes
import com.ivy.file.zip
import com.ivy.impl.export.json.ExportBackupJsonAct
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlin.io.path.Path

class BackupDataAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val exportBackupJsonAct: ExportBackupJsonAct,
    private val googleDrive: GoogleDriveService,
) : Action<BackupDataAct.Input, Either<BackupDataError, BackupDataResult>>() {
    companion object {
        const val DRIVE_BACKUP_PATH = "Ivy-Wallet-DIR/backup/ivy-wallet-backup.zip"
    }

    data class Input(
        val backupFileLocation: Uri
    )

    override suspend fun action(input: Input): Either<BackupDataError, BackupDataResult> =
        either {
            val jsonBackup = exportBackupJsonAct(Unit).toString()
            val backupZipBytes = saveBackupZipFile(jsonBackup, input.backupFileLocation).bind()
            val uploadedToDrive = uploadToDriveIfConnected(backupZipBytes).bind()
            BackupDataResult(
                uploadedToDrive = uploadedToDrive
            )
        }

    private fun saveBackupZipFile(
        jsonBackup: String,
        backupFileLocation: Uri,
    ): Either<BackupDataError, ByteArray> = Either.catch(
        BackupDataError::SaveBackupZipLocally
    ) {
        val backupFile = createJsonDataFile(appContext, jsonBackup)
        zip(appContext, backupFileLocation, listOf(backupFile))
        // TODO: Maybe clear cache dir?
        readFileAsBytes(appContext, backupFileLocation) ?: error("Couldn't read backup zip")
    }

    private fun createJsonDataFile(context: Context, jsonString: String): File {
        val fileNamePrefix = "backup_data"
        val fileNameSuffix = ".json"
        val outputDir = context.cacheDir

        val file = File.createTempFile(fileNamePrefix, fileNameSuffix, outputDir)
        file.writeText(jsonString, Charsets.UTF_16)
        return file
    }

    private suspend fun uploadToDriveIfConnected(
        backupZipBytes: ByteArray
    ): Either<BackupDataError, Boolean> = Either.catch(
        BackupDataError::UploadToDrive
    ) {
        if (googleDrive.isMounted()) {
            googleDrive.write(
                path = Path(DRIVE_BACKUP_PATH),
                content = backupZipBytes,
                mimeType = DriveMimeType.ZIP
            )
            true
        } else false
    }

}

