package com.ivy.wallet

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.ivy.base.legacy.appContext
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ivy.googledrive.backup.BackupWorker
import com.ivy.legacy.domain.deprecated.logic.zip.BackupLogic
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

/**
 * Created by iliyan on 24.02.18.
 */
@HiltAndroidApp
class IvyAndroidApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}

// This solves Could not instantiat worker issue
class CustomWorkerFactory @Inject constructor(
    private val backupLogic: BackupLogic
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            BackupWorker::class.java.name -> BackupWorker(
                appContext,
                workerParameters,
                backupLogic
            )
            else -> null // Return null, so that the base class can delegate to the default WorkerFactory.

        }
    }
}

