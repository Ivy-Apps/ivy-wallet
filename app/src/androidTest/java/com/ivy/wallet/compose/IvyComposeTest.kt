package com.ivy.wallet.compose

import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.ui.IvyActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class IvyComposeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<IvyActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

    private var idlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        idlingResource = TestIdlingResource.idlingResource
        composeTestRule.registerIdlingResource(idlingResource!!)
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            composeTestRule.unregisterIdlingResource(it)
        }
    }
}