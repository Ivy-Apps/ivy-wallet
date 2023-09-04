package com.ivy.wallet.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EncryptedSharedPrefs

@Module
@InstallIn(SingletonComponent::class)
object EncryptedSharedPrefsModule {
    @EncryptedSharedPrefs
    @Provides
    fun provideEncryptedSharedPrefs(
        @ApplicationContext
        appContext: Context
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "ivy_secure_shared_prefs_v1",
            "ivy_master_key_v1",
            appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

object EncryptedPrefsKeys {
    @Deprecated("Use DataStoreKeys.GITHUB_PAT instead")
    const val BACKUP_GITHUB_PAT = "github_backup_pat"
}
