package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BudgetsTests : IvyComposeTest() {

    private val onboardingFlow = OnboardingFlow(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val budgetsScreen = BudgetsScreen(composeTestRule)
    private val budgetModal = BudgetModal(composeTestRule)
    private val deleteConfirmationModal = DeleteConfirmationModal(composeTestRule)

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

        //Delete budget
        budgetModal.clickDelete()
        deleteConfirmationModal.confirmDelete()

        budgetsScreen.assertBudgetsInfo(
            appBudget = null,
            categoryBudget = null
        )

        budgetsScreen.assertBudgetDoesNotExist(
            budgetName = "Living (must)"
        )
    }

    @Test
    fun CreateSeveralBudgets() {
        onboardingFlow.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickBudgets()

        //Add Global budget
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
        budgetModal.clickClose()

        //Add Category budget
        budgetsScreen.clickAddBudget()
        budgetModal.apply {
            enterName("Fun")
            enterAmount("1,000.00")
            clickCategory("Food & Drinks") //only visible categories work
            clickAdd()
        }
        budgetsScreen.assertBudgetsInfo(
            appBudget = "6,000.00",
            categoryBudget = "1,000.00"
        )
        budgetsScreen.clickBudget(
            budgetName = "Fun"
        )
        budgetModal.clickClose()

        //Add Multi-Category Budget
        budgetsScreen.clickAddBudget()
        budgetModal.apply {
            enterName("Must")
            enterAmount("1,750.25")
            clickCategory("Food & Drinks")
            clickCategory("Bills & Fees")
            clickAdd()
        }
        budgetsScreen.assertBudgetsInfo(
            appBudget = "6,000.00",
            categoryBudget = "2,750.25" //1,000 + 1,750.25
        )
        budgetsScreen.clickBudget(
            budgetName = "Must"
        )
        budgetModal.clickClose()
    }
}