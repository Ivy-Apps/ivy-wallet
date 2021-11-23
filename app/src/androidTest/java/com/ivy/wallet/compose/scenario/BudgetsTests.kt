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
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickBudgets()

        budgetsScreen.clickAddBudget()

        budgetModal.apply {
            enterAmount("1,000")
            enterName("Food")
            clickCategory("Food & Drinks")
            clickAdd()
        }

        budgetsScreen.assertBudgetsInfo(
            appBudget = null,
            categoryBudget = "1,000.00"
        )

        budgetsScreen.clickBudget(
            budgetName = "Food"
        )
    }

    @Test
    fun CreateMultiCategoryBudget() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickBudgets()

        budgetsScreen.clickAddBudget()

        budgetModal.apply {
            enterAmount("2,500")
            clickCategory("Food & Drinks")
            clickCategory("Bills & Fees")
            enterName("Living (must)")
            clickAdd()
        }

        budgetsScreen.assertBudgetsInfo(
            appBudget = null,
            categoryBudget = "2,500.00"
        )

        budgetsScreen.clickBudget(
            budgetName = "Living (must)"
        )
    }

    @Test
    fun EditBudget() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickBudgets()

        budgetsScreen.clickAddBudget()

        budgetModal.apply {
            enterName("Spending")
            enterAmount("6000.00")
            clickAdd()
        }

        budgetsScreen.assertBudgetsInfo(
            appBudget = "6,000.00",
            categoryBudget = null
        )

        budgetsScreen.clickBudget(
            budgetName = "Spending"
        )

        //Edit budget
        budgetModal.apply {
            enterName("Fun")
            enterAmount("1,200.99")
            clickCategory("Food & Drinks")
            clickCategory("Bills & Fees")
            clickCategory("Bills & Fees") //de-select "Bills & Fees"
            clickSave()
        }

        budgetsScreen.assertBudgetsInfo(
            appBudget = null,
            categoryBudget = "1,200.99"
        )
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