package com.ivy.wallet.compose.scenario.core

import com.ivy.wallet.compose.IvyComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class DonateTest : IvyComposeTest() {
    @Test
    fun openDonateFromSettings_donate() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSettings()
            .clickDonate()
            .verifyAmount("$5")
            .clickDonate()
    }

    @Test
    fun openDonateFromMoreMenu_reduceToUSD2_donate() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickDonate()
            .clickMinus()
            .verifyAmount("$2")
            .clickDonate()
    }

    @Test
    fun openDonateFromMoreMenu_plusMinus_donate() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSettings()
            .clickDonate()
            .verifyAmount("$5")
            .clickPlus()
            .verifyAmount("$10")
            .clickPlus()
            .verifyAmount("$15")
            .clickMinus()
            .verifyAmount("$10")
            .clickMinus()
            .verifyAmount("$5")
            .clickDonate()
    }

    @Test
    fun openDonateFromMoreMenu_increaseToUSD100_donate() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickDonate()
            .verifyAmount("$5")
            .clickPlus()
            .verifyAmount("$10")
            .clickPlus()
            .verifyAmount("$15")
            .clickPlus()
            .verifyAmount("$25")
            .clickPlus()
            .verifyAmount("$50")
            .clickPlus()
            .verifyAmount("$100")
            .clickDonate()
    }
}