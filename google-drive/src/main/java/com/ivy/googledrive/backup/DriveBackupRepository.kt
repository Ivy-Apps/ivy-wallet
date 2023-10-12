package com.ivy.googledrive.backup

import androidx.work.WorkManager
import com.ivy.googledrive.google_drive.DriveMimeType
import java.io.File
import kotlin.jvm.Throws

interface DriveBackupRepository {
    /**
     * @param n is The capacity of the Backup folder,
     * if the capacity is full then the oldest file will get deleted and get replaced by the new file.
     * */
    @Throws(Exception::class)
    suspend fun backupFile(
        file: File,
        fileName: String,
        mimeType: DriveMimeType,
        n: Int
    ): String? // The returned String here is the id of the backed up file

}