package com.ivy.wallet.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import com.ivy.wallet.compose.helpers.MainBottomBar
import com.ivy.wallet.compose.helpers.OnboardingFlow
import org.junit.Test

class OnboardingTest : IvyComposeTest() {

    @Test
    fun OnboardingShortestPath() {
        val onboarding = OnboardingFlow(composeTestRule)
        val mainBottomBar = MainBottomBar(composeTestRule)

        onboarding.chooseOfflineAccount()
        onboarding.clickStartFresh()
        onboarding.setCurrency()
        onboarding.skipAccounts()
        onboarding.skipCategories()

        mainBottomBar.clickAccounts()

        composeTestRule.onNode(hasText("Cash"))
            .assertIsDisplayed()
    }


}