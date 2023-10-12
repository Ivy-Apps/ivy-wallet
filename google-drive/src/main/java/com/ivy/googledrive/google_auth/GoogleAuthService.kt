package com.ivy.googledrive.google_auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

class GoogleAuthService(private val context: Context) {
    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder()
            .requestEmail()
            .requestProfile()
            .requestScopes(
                Scope(DriveScopes.DRIVE),
                Scope(DriveScopes.DRIVE_FILE),
                Scope(DriveScopes.DRIVE),
            )
            .build()
    }

    private fun getGoogleSignInClient(): GoogleSignInClient {
        return GoogleSignIn.getClient(context, getGoogleSignInOptions())
    }

    fun getSignInIntent(): SignInIntentResult {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (lastSignedInAccount != null) {
            return SignInIntentResult.UserAlreadySignedIn(lastSignedInAccount)
        }
        return SignInIntentResult.NewUserSigning(getGoogleSignInClient().signInIntent)
    }

    fun signOut(
        onLoading: (Boolean) -> Unit,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        onLoading(true)
        getGoogleSignInClient().signOut()
            .addOnSuccessListener {
                onSuccess()
                onLoading(false)
            }
            .addOnFailureListener {
                onError(it)
                onLoading(false)
            }
    }
}