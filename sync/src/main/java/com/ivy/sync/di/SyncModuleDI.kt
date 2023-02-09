package com.ivy.sync.di

import com.ivy.sync.action.RemoteBackupSource
import com.ivy.sync.action.RemoteBackupSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModuleDI {

    @Binds
    abstract fun provideRemoteBackupSource(source: RemoteBackupSourceImpl): RemoteBackupSource
}