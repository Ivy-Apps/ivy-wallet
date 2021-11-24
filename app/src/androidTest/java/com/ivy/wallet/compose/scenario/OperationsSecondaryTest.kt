package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OperationsSecondaryTest : IvyComposeTest() {
    private val onboardingFlow = OnboardingFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val savingsGoalModal = SavingsGoalModal(composeTestRule)
    private val settingsScreen = SettingsScreen(composeTestRule)
    private val nameModal = NameModal(composeTestRule)

    @Test
    fun SetSavingsGoal() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickSavingsGoal()

        savingsGoalModal.apply {
            enterAmount("10,000.00")
            clickSave()
        }

        homeMoreMenu.assertSavingsGoal(
            amount = "10,000.00"
        )

        homeMoreMenu.clickOpenCloseArrow()
        homeTab.assertBalance(
            amount = "0",
            amountDecimal = ".00"
        )
    }

    @Test
    fun EditSavingsGoal() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickSavingsGoal()

        savingsGoalModal.apply {
            enterAmount("550.23")
            clickSave()
        }

        homeMoreMenu.assertSavingsGoal(
            amount = "550.23"
        )

        homeMoreMenu.clickSavingsGoal()
        savingsGoalModal.apply {
            enterAmount("3,500.00")
            clickSave()
        }

        homeMoreMenu.assertSavingsGoal(
            amount = "3,500.00"
        )
    }

    @Test
    fun LockApp_semiTest() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickSettings()

        settingsScreen.clickLockApp()
        settingsScreen.clickLockApp()
    }

    @Test
    fun SetName() {
        onboardingFlow.quickOnboarding()

        homeTab.assertGreeting(
            greeting = "Hi"
        )

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickSettings()

        settingsScreen.assertLocalAccountName(
            name = "Anonymous"
        )
        settingsScreen.clickProfileCard()
        nameModal.apply {
            enterName("Iliyan")
            clickSave()
        }
        settingsScreen.assertLocalAccountName(name = "Iliyan")

        settingsScreen.clickBack()

        homeTab.assertGreeting(
            greeting = "Hi Iliyan"
        )
    }

    @Test
    fun EditName() {
        onboardingFlow.quickOnboarding()

        homeTab.assertGreeting(
            greeting = "Hi"
        )

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickSettings()

        settingsScreen.assertLocalAccountName(
            name = "Anonymous"
        )

        settingsScreen.clickProfileCard()
        nameModal.apply {
            enterName("Iliyan")
            clickSave()
        }
        settingsScreen.assertLocalAccountName(name = "Iliyan")

        settingsScreen.clickProfileCard()
        nameModal.apply {
            enterName("John Doe")
            clickSave()
        }
        settingsScreen.assertLocalAccountName(name = "John Doe")

        settingsScreen.clickBack()

        homeTab.assertGreeting(
            greeting = "Hi John Doe"
        )
    }

    //TODO: Set start date of month test
}