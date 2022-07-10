package com.ivy.wallet.domain.deprecated.logic.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ivy.common.timeNowLocal
import com.ivy.common.toEpochSeconds
import com.ivy.wallet.io.persistence.SharedPrefs
import java.util.concurrent.TimeUnit

@Deprecated("Use FP style, look into `domain.fp` package")
class TransactionReminderLogic(
    private val appContext: Context,
    private val sharedPrefs: SharedPrefs,
) {
    companion object {
        private const val UNIQUE_WORK_NAME_V1 = "transaction_reminder_work"
        private const val UNIQUE_WORK_NAME_V2 = "transaction_reminder_work_v2"
        private const val UNIQUE_WORK_NAME_TEST = "transaction_reminder_work_test"
    }

    fun testNow() {
        val workBuilder = PeriodicWorkRequestBuilder<TransactionReminderWorker>(5, TimeUnit.MINUTES)

        WorkManager
            .getInstance(appContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME_TEST,
                ExistingPeriodicWorkPolicy.REPLACE,
                workBuilder.build()
            )
    }

    fun scheduleReminder() {
        if (!fetchShowNotifications())
            return

        val timeNowLocal = timeNowLocal()
        val today8PM = timeNowLocal()
            .withHour(20)
            .withMinute(0)

        val initialDelaySeconds = if (today8PM.isAfter(timeNowLocal)) {
            //8 PM is in the future, we can start reminder today
            today8PM.toEpochSeconds() - timeNowLocal.toEpochSeconds()
        } else {
            //8 PM has passed, we'll start reminding from tomorrow
            today8PM.plusDays(1).toEpochSeconds() - timeNowLocal.toEpochSeconds()
        }

        val workBuilder = PeriodicWorkRequestBuilder<TransactionReminderWorker>(24, TimeUnit.HOURS)
        if (initialDelaySeconds > 0) {
            workBuilder.setInitialDelay(initialDelaySeconds, TimeUnit.SECONDS)
        }

        WorkManager
            .getInstance(appContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME_V2,
                ExistingPeriodicWorkPolicy.KEEP,
                workBuilder.build()
            )
    }

    private fun fetchShowNotifications(): Boolean =
        sharedPrefs.getBoolean(SharedPrefs.SHOW_NOTIFICATIONS, true)
}