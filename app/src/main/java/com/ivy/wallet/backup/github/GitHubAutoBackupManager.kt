package com.ivy.wallet.backup.github

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GitHubAutoBackupManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val uniqueWorkName = "GITHUB_AUTO_BACKUP_WORK"

    fun scheduleAutoBackups() {
        val initialDelay = calculateInitialDelayMillis()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<GitHubBackupWorker>(
            24, TimeUnit.HOURS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    private fun calculateInitialDelayMillis(): Long {
        val lunchTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val currentTime = Calendar.getInstance()

        return if (currentTime.after(lunchTime)) {
            // If current time is after 12 pm, schedule for next day
            lunchTime.add(Calendar.DAY_OF_YEAR, 1)
            lunchTime.timeInMillis - currentTime.timeInMillis
        } else {
            // If it's before 12 pm, set delay to reach 12 pm
            lunchTime.timeInMillis - currentTime.timeInMillis
        }
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
        const val MAX_RETRIES = 7
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
