package com.ivy.drive.google_drive.di

import com.ivy.drive.google_drive.GoogleDriveConnectionImpl
import com.ivy.drive.google_drive.GoogleDriveProvider
import com.ivy.drive.google_drive.GoogleDriveServiceImpl
import com.ivy.drive.google_drive.api.GoogleDriveConnection
import com.ivy.drive.google_drive.api.GoogleDriveService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleDriveModuleDI {
    @Binds
    internal abstract fun googleDriveService(impl: GoogleDriveServiceImpl): GoogleDriveService

    @Binds
    internal abstract fun googleDriveInitializer(impl: GoogleDriveConnectionImpl): GoogleDriveConnection

    @Binds
    internal abstract fun googleDriveProvider(impl: GoogleDriveConnectionImpl): GoogleDriveProvider
}