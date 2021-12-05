package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.compose.printTree
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.ui.theme.Purple2
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class LoansTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val loansScreen = LoansScreen(composeTestRule)
    private val loanModal = LoanModal(composeTestRule)
    private val loanDetailsScreen = LoanDetailsScreen(composeTestRule)

    @Test
    fun CreateLoan() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoan()
        loanModal.apply {
            enterName("Loan 1")
            enterAmount("4,800.32")
            selectLoanType(LoanType.BORROW)
            colorPicker.chooseColor(Purple2)
            chooseIconFlow.chooseIcon("education")

            clickAdd()
        }

        composeTestRule.printTree()
        loansScreen.assertLoan(
            name = "Loan 1",
            amount = "4,800",
            amountDecimal = ".32",
            loanType = LoanType.BORROW,
            currency = "USD",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
    }

    @Test
    fun EditLoan() {

    }

    @Test
    fun CreateSeveralLoans() {

    }

    @Test
    fun DeleteLoanWithNoRecrods() {

    }

    //Loan records ---------------------------------------------------------------------------------
    @Test
    fun AddLoanRecord() {

    }

    @Test
    fun EditLoanRecord() {

    }

    @Test
    fun DeleteLoanRecord() {

    }

    @Test
    fun AddSeveralLoanRecords() {

    }

    @Test
    fun DeleteLoanWithRecrods() {

    }
}