package com.ivy.wallet.backup.github

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GitHubAutoBackupManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val uniqueWorkName = "GITHUB_AUTO_BACKUP_WORK"

    fun scheduleAutoBackups() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<GitHubBackupWorker>(
            6,
            TimeUnit.HOURS
        ).setInitialDelay(30, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                60,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    fun cancelAutoBackups() {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}

@HiltWorker
class GitHubBackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val gitHubBackup: GitHubBackup,
) : CoroutineWorker(appContext, params) {
    companion object {
        const val MAX_RETRIES = 15
    }

    override suspend fun doWork(): Result {
        return gitHubBackup.backupData(
            commitMsg = "Automatic Ivy Wallet data backup"
        ).fold(
            ifLeft = {
                if (runAttemptCount <= MAX_RETRIES) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            },
            ifRight = {
                Result.success()
            },
        )
    }
}
