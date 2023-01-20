package com.ivy.drive.google_drive.api

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.StateFlow

interface GoogleDriveConnection {
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
}