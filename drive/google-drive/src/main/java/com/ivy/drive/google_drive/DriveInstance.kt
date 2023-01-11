package com.ivy.drive.google_drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

private const val APP_NAME = "Ivy Wallet"

fun driveInstance(
    context: Context,
    googleAccount: GoogleSignInAccount
): Drive? {
    // Use the authenticated account to sign in to the Drive service.
    val credential = GoogleAccountCredential.usingOAuth2(
        context, listOf(DriveScopes.DRIVE_FILE)
    )
    credential.selectedAccount = googleAccount.account
    return Drive.Builder(
        AndroidHttp.newCompatibleTransport(),
        JacksonFactory.getDefaultInstance(),
        credential
    )
        .setApplicationName(APP_NAME)
        .build()
}