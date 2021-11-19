package com.ivy.wallet.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import org.junit.Test

class OnboardingTest : IvyComposeTest() {

    @Test
    fun OfflineAccountSkipSkip() {
        chooseOfflineAccount()
    }

    private fun chooseOfflineAccount() {
        composeTestRule.onNode(hasText("Offline account"))
            .performClick()

        composeTestRule.onNode(hasText("Import CSV file"))
            .assertIsDisplayed()
    }
}