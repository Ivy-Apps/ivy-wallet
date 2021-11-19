package com.ivy.wallet.compose

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ivy.wallet.ui.IvyActivity
import org.junit.Rule

abstract class IvyComposeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<IvyActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to an activity

}