package com.ivy.drive.google_drive.data


import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.drive.google_drive.domain.GoogleDriveService
import com.google.api.services.drive.model.File
import com.ivy.drive.google_drive.util.GoogleDriveUtil.APP_NAME
import timber.log.Timber
import java.io.OutputStream

internal class GoogleDriveServiceImpl : GoogleDriveService {

    private var driveHelper: GoogleDriveServiceHelper? = null

    override suspend fun upload(
        file: File,
        fileContent: FileContent,
        driveFiletype: GoogleDriveFileType
    ): String {
        return try{
            driveHelper!!.uploadFileAsync(
                file = file,
                fileContent = fileContent,
                driveFiletype = driveFiletype
            ).await()
        } catch (e: Exception) {
            "Error while uploading a file : ${e.printStackTrace()}"
        }
    }

    override suspend fun download(fileName: String): OutputStream? {
        return driveHelper!!.downloadFile(fileName)
    }

    override fun handleSignInResult(context: Context) {
        initDriveHelper(context)
    }

    override fun requestSignIn(): GoogleSignInOptions {
        return handleRequestSignIn()
    }


    private fun handleRequestSignIn(): GoogleSignInOptions {
        Timber.d("Requesting sign-in")
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("364763737033-t1d2qe7s0s8597k7anu3sb2nq79ot5tp.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
    }

    private fun initDriveHelper(context: Context) {
        GoogleSignIn.getLastSignedInAccount(context).let { googleSignInAccount ->

            // Use the authenticated account to sign in to the Drive service.
            val credential = GoogleAccountCredential.usingOAuth2(
                context, listOf(DriveScopes.DRIVE_FILE)
            )
            Timber.d("googleSignInAccount value $googleSignInAccount")
            if (googleSignInAccount != null) {
                credential.selectedAccount = googleSignInAccount.account
            }
            val googleDriveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(APP_NAME)
                .build()

            // The DriveServiceHelper encapsulates all REST API and SAF functionality.
            // Its instantiation is required before handling any onClick actions.
            driveHelper = GoogleDriveServiceHelper(googleDriveService)

        }
    }


//    private fun mockFilesAndUpload() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val file = createGoogleDriveFile(
//                "IvyImageFile",
//                getParents(GoogleDriveFileType.Image.FOLDER_NAME),
//                GoogleDriveType.BackupTypePlainText.VALUE
//            )
//
//            println(GoogleDriveFileType.Backup().toString())
//            val content = "Hey GDrive file Upload was a success Congrats!!"
//
//            val textFile = java.io.File(content)
//            val fileContents = FileContent("text/plain",textFile)
//
//            Timber.d("Uploading the file")
//
//            val result = upload(
//                file = file,
//                fileContent = fileContents,
//                driveFiletype = GoogleDriveFileType.Image(
//                    GoogleDriveType.BackupTypePlainText
//                )
//            )
//
//            Timber.d("File uploaded with id : $result")
//
//        }
//    }

}