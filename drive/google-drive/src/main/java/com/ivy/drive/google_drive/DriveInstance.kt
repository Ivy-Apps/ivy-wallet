package com.ivy.drive.google_drive

import android.content.Context
import arrow.core.Either
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.drive.google_drive.data.GoogleDriveError

private const val APP_NAME = "Ivy Wallet"

internal fun driveInstance(
    context: Context,
    googleAccount: GoogleSignInAccount
): Either<GoogleDriveError, Drive> = Either.catch {
    // Use the authenticated account to sign in to the Drive service.
    val credential = GoogleAccountCredential.usingOAuth2(
        context, listOf(DriveScopes.DRIVE_FILE)
    )
    credential.selectedAccount = googleAccount.account
    Drive.Builder(
        AndroidHttp.newCompatibleTransport(),
        JacksonFactory.getDefaultInstance(),
        credential
    )
        .setApplicationName(APP_NAME)
        .build()
}.mapLeft(GoogleDriveError::NotMounted)