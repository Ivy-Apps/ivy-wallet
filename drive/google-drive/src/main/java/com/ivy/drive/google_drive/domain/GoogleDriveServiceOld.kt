package com.ivy.drive.google_drive.domain

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.File
import com.ivy.drive.google_drive.data.GoogleDriveFileType
import java.io.OutputStream

@Deprecated("old")
interface GoogleDriveServiceOld {

    companion object {
        const val IVY_DEFAULT_BACKUP_FILE_NAME: String = "IvyBackupFile"
        const val IVY_DEFAULT_IMAGE_FILE_NAME: String = "IvyImageFile"
    }

    suspend fun upload(
        file: File,
        fileContent: FileContent,
        driveFiletype: GoogleDriveFileType
    ): String

    // TODO: Refactor how to download a file
    suspend fun download(
        fileName: String
    ): OutputStream?

    fun handleSignInResult(
        context: Context
    )

    fun requestSignIn():
            GoogleSignInOptions
}