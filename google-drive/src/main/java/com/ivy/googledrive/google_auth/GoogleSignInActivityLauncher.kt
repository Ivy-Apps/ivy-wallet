package com.ivy.googledrive.google_auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.lang.Exception

@Composable
fun GoogleSignInActivityLauncher(
    onResult: (GoogleSignInAccount) -> Unit,
    onError: (Exception?) -> Unit,
    onCancel: () -> Unit,
    content: @Composable (ManagedActivityResultLauncher<Intent, ActivityResult>) -> Unit
) {
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(
                    task = task,
                    onResult = onResult,
                    onError = onError
                )
            } else {
                onCancel()
            }
        }
    )
    content(googleSignInLauncher)
}

private fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    onResult: (GoogleSignInAccount) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val account = task.getResult(ApiException::class.java)
        onResult(account)
    } catch (e: ApiException) {
        onError(e)
    }
}