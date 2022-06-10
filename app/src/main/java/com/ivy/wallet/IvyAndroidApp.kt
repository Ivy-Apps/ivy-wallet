package com.ivy.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

/**
 * Created by iliyan on 24.02.18.
 */
@HiltAndroidApp
class IvyAndroidApp : Application(), Configuration.Provider {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
    }


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

fun stringRes(
    @StringRes id: Int,
    vararg args: String
): String {
    //I don't want strings.xml to handle something different than String at this point
    return IvyAndroidApp.appContext.getString(id, *args)
}

fun refreshWidget(widgetReceiver: Class<out AppWidgetProvider>) {
    val updateIntent = Intent(IvyAndroidApp.appContext, widgetReceiver)
    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val widgetManager = AppWidgetManager.getInstance(IvyAndroidApp.appContext)
    val ids = widgetManager.getAppWidgetIds(
        ComponentName(
            IvyAndroidApp.appContext,
            widgetReceiver
        )
    )
    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

    IvyAndroidApp.appContext.sendBroadcast(updateIntent)
}