package com.ivy.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
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