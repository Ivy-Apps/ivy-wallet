package com.ivy.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ivy.data.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    @Provides
    fun provideDatastore(
        @ApplicationContext
        context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
}