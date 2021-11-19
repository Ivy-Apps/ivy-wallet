package com.ivy.wallet

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ivy.wallet.ui.IvyActivity
import org.junit.Rule
import org.junit.Test


class MyComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<IvyActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

    @Test
    fun MyTest() {
        composeTestRule.printTree()
    }
}