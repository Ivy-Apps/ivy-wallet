package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class OnboardingFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun chooseOfflineAccount() {
        composeTestRule.onNode(hasText("Offline account"))
            .performClick()

        composeTestRule.onNode(hasText("Import CSV file"))
            .assertIsDisplayed()
    }

    fun clickStartFresh() {
        composeTestRule.onNode(hasText("Start fresh"))
            .performClick()

        composeTestRule.onNode(hasText("Set currency"))
            .assertIsDisplayed()
    }

    fun setCurrency() {
        composeTestRule.onNode(hasText("Set"))
            .performClick()

        composeTestRule.onNode(hasText("Add accounts"))
            .assertIsDisplayed()
    }

    fun skipAccounts() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Add categories"))
            .assertIsDisplayed()
    }

    fun skipCategories() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Hi"))
            .assertIsDisplayed()
    }

    fun quickOnboarding() {
        chooseOfflineAccount()
        clickStartFresh()
        setCurrency()
        skipAccounts()
        skipCategories()
    }
}