package com.ivy.drive.google_drive.di

import com.google.api.services.drive.Drive
import com.ivy.drive.google_drive.data.GoogleDriveServiceHelper
import com.ivy.drive.google_drive.data.GoogleDriveServiceImpl
import com.ivy.drive.google_drive.domain.GoogleDriveService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleDriveModuleDI {

    @Singleton
    @Provides
    fun provideGoogleDriveService(): GoogleDriveService =
        GoogleDriveServiceImpl()

}