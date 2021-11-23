package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.BudgetModal
import com.ivy.wallet.compose.helpers.BudgetsScreen
import com.ivy.wallet.compose.helpers.HomeMoreMenu
import com.ivy.wallet.compose.helpers.OnboardingFlow
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BudgetsTests : IvyComposeTest() {

    private val onboardingFlow = OnboardingFlow(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val budgetsScreen = BudgetsScreen(composeTestRule)
    private val budgetModal = BudgetModal(composeTestRule)

    @Test
    fun CreateGlobalBudget() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickBudgets()

        budgetsScreen.clickAddBudget()

        budgetModal.apply {
            enterName("Spending")
            enterAmount("6,000.00")
            clickAdd()
        }

        budgetsScreen.assertBudgetsInfo(
            appBudget = "6,000.00",
            categoryBudget = null
        )

        budgetsScreen.clickBudget(
            budgetName = "Spending"
        )
    }

    @Test
    fun CreateCategoryBudget() {
        TODO("Implement")
    }

    @Test
    fun CreateMultiCategoryBudget() {
        TODO("Implement")
    }

    @Test
    fun EditBudget() {
        //amount
        //categories
        //name

        TODO("Implement")
    }

    @Test
    fun DeleteBudget() {
        TODO("Implement")
    }

    @Test
    fun CreateSeveralBudgets() {
        TODO("Implement")
    }
}