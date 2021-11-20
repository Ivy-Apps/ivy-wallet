package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class MainScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun clickAccounts() {
        composeTestRule.onNode(hasTestTag("accounts"))
            .performClick()
    }
}