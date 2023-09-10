package com.ivy.wallet

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Intent
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ivy.core.appContext
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
    lateinit var workerFactory: HiltWorkerFactory

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

fun refreshWidget(widgetReceiver: Class<out AppWidgetProvider>) {
    val updateIntent = Intent(appContext, widgetReceiver)
    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val widgetManager = AppWidgetManager.getInstance(appContext)
    val ids = widgetManager.getAppWidgetIds(
        ComponentName(
            appContext,
            widgetReceiver
        )
    )
    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

    appContext.sendBroadcast(updateIntent)
}
