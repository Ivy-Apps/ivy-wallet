package com.ivy.wallet.compose

import android.content.Context
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.TestingContext
import com.ivy.wallet.persistence.IvyRoomDatabase
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.ui.IvyActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class IvyComposeTest {
    //TODO: Setup Hilt, too
    //https://developer.android.com/training/dependency-injection/hilt-testing

    @get:Rule
    val composeTestRule = createAndroidComposeRule<IvyActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

    private var idlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        idlingResource = TestIdlingResource.idlingResource
        composeTestRule.registerIdlingResource(idlingResource!!)
        TestingContext.inTest = true
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            composeTestRule.unregisterIdlingResource(it)
        }

        TestingContext.inTest = false
        resetApp()
    }

    protected fun resetApp() {
        clearSharedPrefs()
        deleteDatabase()
    }

    protected fun clearSharedPrefs() {
        SharedPrefs(targetContext()).removeAll()
    }

    protected fun deleteDatabase() {
        targetContext().deleteDatabase(IvyRoomDatabase.DB_NAME)
    }

    private fun targetContext(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

}