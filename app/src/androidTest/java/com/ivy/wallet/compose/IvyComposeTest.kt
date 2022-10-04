package com.ivy.wallet.compose

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.ivy.common.test.epocMillisNow
import com.ivy.common.test.epochSecondsNow
import com.ivy.core.domain.test.TestIdlingResource
import com.ivy.core.domain.test.TestingContext
import com.ivy.core.ui.temp.trash.IvyWalletCtx
import com.ivy.wallet.compose.component.OnboardingFlow
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

typealias IvyComposeTestRule = AndroidComposeTestRule<ActivityScenarioRule<RootActivity>, RootActivity>

@HiltAndroidTest
abstract class IvyComposeTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

    private var idlingResource: IdlingResource? = null

    @Inject
    lateinit var ivyContext: IvyWalletCtx

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
        resetTestIdling()

        TestingContext.inTest = false

        resetApp()
    }

    private fun resetTestIdling() {
        idlingResource?.let {
            composeTestRule.unregisterIdlingResource(it)
        }
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

    private fun resetDatabase() = runTest {
        ivyRoomDatabase.reset()
    }

    private fun resetIvyContext() {
        ivyContext.reset()
    }

    private fun context(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    protected fun testDebug(
        test: OnboardingFlow.() -> Unit
    ) = testWithRetry(maxAttempts = 0, test = test)

    protected fun testWithRetry(
        attempt: Int = 0,
        maxAttempts: Int = 3,
        firstFailure: Throwable? = null,
        test: OnboardingFlow.() -> Unit
    ) {
        try {
            OnboardingFlow(composeTestRule).test()
        } catch (e: Throwable) {
            if (attempt < maxAttempts) {
                //reset state && retry test
                resetApp()

                composeTestRule.waitMillis(300) //wait for resetting app to finish
                TestIdlingResource.reset()

                //Restart IvyActivity
                composeTestRule.activityRule.scenario.recreate()

                composeTestRule.waitMillis(300) //wait for activity to start

                testWithRetry(
                    attempt = attempt + 1,
                    maxAttempts = maxAttempts,
                    firstFailure = if (attempt == 0) e else firstFailure,
                    test = test
                )
            } else {
                //propagate exception
                throw firstFailure ?: e
            }
        }
    }
}

fun ComposeTestRule.waitSeconds(secondsToWait: Long) {
    val secondsStart = epochSecondsNow()
    this.waitUntil(timeoutMillis = (secondsToWait + 5) * 1000) {
        secondsStart - epochSecondsNow() < -secondsToWait
    }
}

fun ComposeTestRule.waitMillis(waitMs: Long) {
    val startMs = epocMillisNow()
    this.waitUntil(timeoutMillis = waitMs + 5000) {
        startMs - epocMillisNow() < -waitMs
    }
}

fun SemanticsNodeInteraction.performClickWithRetry(
    composeTestRule: ComposeTestRule
) {
    composeTestRule.clickWithRetry(
        node = this,
        maxRetries = 3
    )
}

fun ComposeTestRule.clickWithRetry(
    node: SemanticsNodeInteraction,
    retryAttempt: Int = 0,
    maxRetries: Int = 15,
    waitBetweenRetriesMs: Long = 100,
) {
    try {
        node.assertExists()
            .performClick()
    } catch (e: AssertionError) {
        if (retryAttempt < maxRetries) {
            waitMillis(waitBetweenRetriesMs)

            clickWithRetry(
                node = node,
                retryAttempt = retryAttempt + 1,
                maxRetries = maxRetries,
                waitBetweenRetriesMs = waitBetweenRetriesMs
            )
        }
    }
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.hideKeyboard() {
    with(this.activity) {
        if (currentFocus != null) {
            val inputMethodManager: InputMethodManager =
                this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}