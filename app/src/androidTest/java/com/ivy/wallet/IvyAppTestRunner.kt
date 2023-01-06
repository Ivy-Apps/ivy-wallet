package com.ivy.wallet

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

@Suppress("UNUSED")
class IvyAppTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context): Application {
        IvyAndroidApp.appContext = context
        com.ivy.core.ui.temp.GlobalProvider.appContext = context
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}