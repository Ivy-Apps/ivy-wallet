package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.waitSeconds
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
class OperationsSecondaryTest : IvyComposeTest() {

    @Test
    fun SetSavingsGoal() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSavingsGoal()
            .enterAmount("10,000.00")
            .clickSave()
            .assertSavingsGoal(
                amount = "10,000.00"
            )
            .closeMoreMenu()
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
    }

    @Test
    fun EditSavingsGoal() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSavingsGoal()
            .enterAmount("550.23")
            .clickSave()
            .assertSavingsGoal(
                amount = "550.23"
            )
            .clickSavingsGoal()
            .enterAmount("3,500.00")
            .clickSave()
            .assertSavingsGoal(
                amount = "3,500.00"
            )
    }

    @Ignore("performScrollTo() + click doesn't work")
    @Test
    fun LockApp_semiTest() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSettings()
            .clickLockApp()
            .clickLockApp()
    }

    @Test
    fun SetName_LocalAccount() = testWithRetry {
        quickOnboarding()
            .assertGreeting(
                greeting = "Hi"
            )
            .openMoreMenu()
            .clickSettings()
            .assertLocalAccountName(
                name = "Anonymous"
            )
            .clickProfileCard()
            .enterName("Iliyan")
            .clickSave()
            .assertLocalAccountName(name = "Iliyan")
            .clickBack()
            .closeMoreMenu()
            .assertGreeting(
                greeting = "Hi Iliyan"
            )
    }

    @Test
    fun EditName_LocalAccount() = testWithRetry {
        quickOnboarding()
            .assertGreeting(
                greeting = "Hi"
            )
            .openMoreMenu()
            .clickSettings()
            .assertLocalAccountName(
                name = "Anonymous"
            )
            .clickProfileCard()
            .enterName("Iliyan")
            .clickSave()
            .assertLocalAccountName(name = "Iliyan")
            .clickProfileCard()
            .enterName("John Doe")
            .clickSave()
            .assertLocalAccountName(name = "John Doe")
            .clickBack()
            .closeMoreMenu()
            .assertGreeting(
                greeting = "Hi John Doe"
            )
    }

    @Ignore("performScrollTo() + click doesn't work")
    @Test
    fun SetStartDateOfMonth_semiTest() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickSettings()
            //TODO: Fix test scroll + click doesn't work
            .clickStartDateOfMonth()
        composeTestRule.waitSeconds(5)
    }
}