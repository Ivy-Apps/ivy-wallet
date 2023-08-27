package com.ivy.wallet.backup.github

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
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
        val initialDelay = calculateInitialDelay()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<GitHubBackupWorker>(
            24, TimeUnit.HOURS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    private fun calculateInitialDelay(): Long {
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

class GitHubBackupWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Your task here...
        return Result.success()
    }
}
