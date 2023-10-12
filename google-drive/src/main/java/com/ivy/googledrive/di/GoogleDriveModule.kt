package com.ivy.googledrive.di

import android.app.Application
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ivy.googledrive.backup.DriveBackupRepository
import com.ivy.googledrive.backup.DriveBackupRepositoryImpl
import com.ivy.googledrive.google_auth.GoogleAuthService
import com.ivy.googledrive.google_drive.GoogleDriveService
import com.ivy.googledrive.google_drive.GoogleDriveServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleDriveModule {
    @Provides
    @Singleton
    fun provideWorkManager(
        application: Application
    ): WorkManager = WorkManager.getInstance(application)

    @Provides
    @Singleton
    fun provideGoogleAuthService(application: Application): GoogleAuthService =
        GoogleAuthService(application)

}