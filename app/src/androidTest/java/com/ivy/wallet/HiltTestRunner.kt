package com.ivy.wallet

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// A custom runner to set up the instrumented application class for tests.
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context): Application {
        IvyAndroidApp.appContext = context
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}