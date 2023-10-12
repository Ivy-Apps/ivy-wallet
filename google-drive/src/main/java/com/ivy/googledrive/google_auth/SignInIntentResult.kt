package com.ivy.googledrive.google_auth

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

sealed interface SignInIntentResult {

    data class UserAlreadySignedIn(val googleAccount: GoogleSignInAccount) : SignInIntentResult

    data class NewUserSigning(val loginIntent: Intent) : SignInIntentResult

}