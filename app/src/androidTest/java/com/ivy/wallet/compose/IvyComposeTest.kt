package com.ivy.wallet.compose

import android.content.Context
import android.util.Log
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.TestingContext
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.base.toEpochSeconds
import com.ivy.wallet.persistence.IvyRoomDatabase
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.ui.IvyActivity
import com.ivy.wallet.ui.IvyContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
abstract class IvyComposeTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<IvyActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

    private var idlingResource: IdlingResource? = null

    @Inject
    lateinit var ivyContext: IvyContext

    @Inject
    lateinit var ivyRoomDatabase: IvyRoomDatabase

    @Inject
    lateinit var ivySession: IvySession

    @Before
    fun setUp() {
        setupTestIdling()
        setupHiltDI()

        TestingContext.inTest = true
    }

    private fun setupTestIdling() {
        TestIdlingResource.reset()
        idlingResource = TestIdlingResource.idlingResource
        composeTestRule.registerIdlingResource(idlingResource!!)
    }

    private fun setupHiltDI() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context(), config)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            composeTestRule.unregisterIdlingResource(it)
        }

        TestingContext.inTest = false

        resetApp()
    }

    private fun resetApp() {
        clearSharedPrefs()
        resetDatabase()
        resetIvyContext()
        ivySession.logout()
    }

    private fun clearSharedPrefs() {
        SharedPrefs(context()).removeAll()
    }

    private fun resetDatabase() {
        ivyRoomDatabase.reset()
    }

    private fun resetIvyContext() {
        ivyContext.reset()
    }

    private fun context(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
}

fun ComposeTestRule.wait(secondsToWait: Long) {
    val secondsStart = timeNowUTC().toEpochSeconds()
    this.waitUntil(timeoutMillis = (secondsToWait + 5) * 1000) {
        secondsStart - timeNowUTC().toEpochSeconds() < -secondsToWait
    }
}