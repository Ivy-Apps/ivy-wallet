package com.ivy.wallet.compose.scenario

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
}