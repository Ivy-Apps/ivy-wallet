package com.ivy.drive.google_drive

import androidx.appcompat.app.AppCompatActivity
import arrow.core.Either
import com.google.api.services.drive.Drive
import com.ivy.drive.google_drive.data.GoogleDriveError
import kotlinx.coroutines.flow.StateFlow

interface GoogleDriveInitializer {
    /**
     * Call in Activity's onCreate to register ActivityResultLauncher
     */
    fun wire(activity: AppCompatActivity)

    /**
     * Prompts the user to login with Google and mounts the drive
     */
    fun connect()

    /**
     * Mounts drive if it's connected
     */
    suspend fun mount()

    // TODO: Instead of boolean return Option<DriveInfo> which
    //  contains the email of the mounted drive so it can be displayed in the UI
    val driveMounted: StateFlow<Boolean>
    val errorOrDrive: Either<GoogleDriveError, Drive>
}