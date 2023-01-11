package com.ivy.drive.google_drive.di

import com.ivy.drive.google_drive.GoogleDriveService
import com.ivy.drive.google_drive.GoogleDriveServiceImpl
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