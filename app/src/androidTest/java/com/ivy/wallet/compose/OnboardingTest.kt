package com.ivy.wallet.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import org.junit.Test

class OnboardingTest : IvyComposeTest() {

    @Test
    fun OnboardingShortestPath() {
        chooseOfflineAccount()

        clickStartFresh()

        setCurrency()

        skipAccounts()

        skipCategories()

        composeTestRule.printTree()
    }

    private fun chooseOfflineAccount() {
        composeTestRule.onNode(hasText("Offline account"))
            .performClick()

        composeTestRule.onNode(hasText("Import CSV file"))
            .assertIsDisplayed()
    }

    private fun clickStartFresh() {
        composeTestRule.onNode(hasText("Start fresh"))
            .performClick()

        composeTestRule.onNode(hasText("Set currency"))
            .assertIsDisplayed()
    }

    private fun setCurrency() {
        composeTestRule.onNode(hasText("Set"))
            .performClick()

        composeTestRule.onNode(hasText("Add accounts"))
            .assertIsDisplayed()
    }

    private fun skipAccounts() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Add categories"))
            .assertIsDisplayed()
    }

    private fun skipCategories() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Hi"))
            .assertIsDisplayed()
    }
}