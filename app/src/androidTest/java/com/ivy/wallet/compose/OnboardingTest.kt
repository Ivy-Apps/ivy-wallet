package com.ivy.wallet.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class OnboardingTest : IvyComposeTest() {

    @Test
    fun OfflineAccountSkipSkip(): Unit = runBlocking {
        chooseOfflineAccount()
    }

    private suspend fun chooseOfflineAccount() {
        //Wait for IvyViewModel to route from SPLASH to LOGIN
        delay(2000)

        composeTestRule.onNode(hasText("Offline account")).performClick()

        composeTestRule.onNode(hasText("Import CSV file"))
            .assertIsDisplayed()
    }
}