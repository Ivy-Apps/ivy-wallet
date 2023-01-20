package com.ivy.drive.google_drive

import android.content.Context
import android.content.Intent
import arrow.core.Either
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.android.common.ActivityLauncher
import com.ivy.drive.google_drive.data.GoogleDriveError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class MountDriveLauncher @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) : ActivityLauncher<Unit, Either<GoogleDriveError, Drive>>() {
    override fun intent(context: Context, input: Unit): Intent {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken("364763737033-t1d2qe7s0s8597k7anu3sb2nq79ot5tp.apps.googleusercontent.com")
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        return GoogleSignIn.getClient(context, signInOptions).signInIntent
    }

    override fun onActivityResult(
        resultCode: Int,
        intent: Intent?
    ): Either<GoogleDriveError, Drive> =
        try {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            driveInstance(context = appContext, googleAccount = account)
        } catch (e: ApiException) {
            e.printStackTrace()
            Either.Left(GoogleDriveError.NotMounted(e))
        }

}