package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BudgetsTest : IvyComposeTest() {

    @Test
    fun CreateGlobalBudget() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            .clickAddBudget()
            .enterName("Spending")
            .enterAmount("6,000.00")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = "6,000.00",
                categoryBudget = null
            )
            .clickBudget(
                budgetName = "Spending"
            )
    }

    @Test
    fun CreateCategoryBudget() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            .clickAddBudget()
            .enterAmount("1,000")
            .enterName("Food")
            .clickCategory("Food & Drinks")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = null,
                categoryBudget = "1,000.00"
            )
            .clickBudget(
                budgetName = "Food"
            )
    }

    @Test
    fun CreateMultiCategoryBudget() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            .clickAddBudget()
            .enterAmount("2,500")
            .clickCategory("Food & Drinks")
            .clickCategory("Bills & Fees")
            .enterName("Living (must)")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = null,
                categoryBudget = "2,500.00"
            )
            .clickBudget(
                budgetName = "Living (must)"
            )
    }

    @Test
    fun EditBudget() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            .clickAddBudget()
            .enterName("Spending")
            .enterAmount("6000.00")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = "6,000.00",
                categoryBudget = null
            )
            .clickBudget(
                budgetName = "Spending"
            )
            //Edit budget
            .enterName("Fun")
            .enterAmount("1,200.99")
            .clickCategory("Food & Drinks")
            .clickCategory("Bills & Fees")
            .clickCategory("Bills & Fees") //de-select "Bills & Fees"
            .clickSave()
            .assertBudgetsInfo(
                appBudget = null,
                categoryBudget = "1,200.99"
            )
    }

    @Test
    fun DeleteBudget() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            .clickAddBudget()
            .enterAmount("2,500")
            .clickCategory("Food & Drinks")
            .clickCategory("Bills & Fees")
            .enterName("Living (must)")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = null,
                categoryBudget = "2,500.00"
            )
            .clickBudget(
                budgetName = "Living (must)"
            )

            //Delete budget
            .deleteWithConfirmation()
            .assertBudgetsInfo(
                appBudget = null,
                categoryBudget = null
            )
            .assertBudgetDoesNotExist(
                budgetName = "Living (must)"
            )
    }

    @Test
    fun CreateSeveralBudgets() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickBudgets()
            //Add Global budget
            .clickAddBudget()
            .enterName("Spending")
            .enterAmount("6,000.00")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = "6,000.00",
                categoryBudget = null
            )
            .clickBudget(
                budgetName = "Spending"
            )
            .clickClose()
            //Add Category budget
            .clickAddBudget()
            .enterName("Fun")
            .enterAmount("1,000.00")
            .clickCategory("Food & Drinks") //only visible categories work
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = "6,000.00",
                categoryBudget = "1,000.00"
            )
            .clickBudget(
                budgetName = "Fun"
            )
            .clickClose()
            //Add Multi-Category Budget
            .clickAddBudget()
            .enterName("Must")
            .enterAmount("1,750.25")
            .clickCategory("Food & Drinks")
            .clickCategory("Bills & Fees")
            .clickAdd()
            .assertBudgetsInfo(
                appBudget = "6,000.00",
                categoryBudget = "2,750.25" //1,000 + 1,750.25
            )
            .clickBudget(
                budgetName = "Must"
            )
            .clickClose()
    }
}