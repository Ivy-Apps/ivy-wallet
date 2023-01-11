package com.ivy.drive.google_drive.di

import com.ivy.drive.google_drive.drivev2.GoogleDriveService
import com.ivy.drive.google_drive.drivev2.GoogleDriveServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleDriveModuleDI {
    @Binds
    abstract fun googleDriveService(impl: GoogleDriveServiceImpl): GoogleDriveService
}