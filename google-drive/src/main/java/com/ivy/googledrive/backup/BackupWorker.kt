package com.ivy.googledrive.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.googledrive.backup.DriveBackupConstants.IVY_BACKUP_FILE_NAME
import com.ivy.googledrive.google_drive.DriveMimeType
import com.ivy.googledrive.google_drive.GoogleDriveServiceImpl
import com.ivy.legacy.domain.deprecated.logic.zip.BackupLogic
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.util.Date

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupLogic: BackupLogic
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val TAG = "BackupWorker"
    }

    override suspend fun doWork(): Result {
        try {
            val driveBackupRepository = getDriveBackRepository(appContext)
            val jsonContent = backupLogic.generateJsonBackup()
            val backupFile = File(appContext.filesDir, "${IVY_BACKUP_FILE_NAME}.json")
            backupFile.writeText(jsonContent)
            val fileId = driveBackupRepository.backupFile(
                backupFile,
                "${IVY_BACKUP_FILE_NAME}-${Date()}",
                DriveMimeType.JSON,
                5
            )
            return if (fileId != null) {
                Timber.tag(TAG).d("Backed up user's data")
                Result.success()
            } else {
                Timber.tag(TAG).e("Error while backing up the user's data")
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e("Error while backing up the user's data")
            e.printStackTrace()
            return Result.failure()
        }
    }

    // The reason of why i did not use hilt to inject this repo in the constructor is because i want to get the drive instance during
    // The runtime, if we use hilt to inject this in the constructor the drive instance will be null when the user links his google account
    // For the first time.
    private fun getDriveBackRepository(context: Context): DriveBackupRepository {
        val drive = GoogleSignIn.getLastSignedInAccount(context).let { googleAccount ->
            val credential =
                GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = googleAccount?.account
            Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("Ivy Wallet")
                .build()
        }
        return DriveBackupRepositoryImpl(GoogleDriveServiceImpl(drive))
    }
}