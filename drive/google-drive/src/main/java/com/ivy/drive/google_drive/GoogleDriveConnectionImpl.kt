package com.ivy.drive.google_drive

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import arrow.core.Either
import arrow.core.left
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.services.drive.Drive
import com.ivy.drive.google_drive.api.GoogleDriveConnection
import com.ivy.drive.google_drive.data.GoogleDriveError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GoogleDriveConnectionImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val mountDriveLauncher: MountDriveLauncher
) : GoogleDriveConnection, GoogleDriveProvider {
    private val _isMounted = MutableStateFlow(false)
    override val driveMounted: StateFlow<Boolean> = _isMounted

    override var errorOrDrive: Either<GoogleDriveError, Drive> =
        GoogleDriveError.NotMounted(IllegalStateException("Drive not mounted")).left()

    override fun wire(activity: AppCompatActivity) = mountDriveLauncher.wire(activity)

    override fun connect() = mountDriveLauncher.launch(Unit) { drive ->
        mountInternal(drive)
    }

    override suspend fun mount() {
        try {
            GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
                val drive = driveInstance(context, googleAccount)
                mountInternal(drive)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mountInternal(drive: Either<GoogleDriveError, Drive>) {
        this.errorOrDrive = drive
        _isMounted.value = drive.isRight()
    }
}