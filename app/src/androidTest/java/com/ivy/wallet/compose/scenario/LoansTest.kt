package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.ui.theme.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class LoansTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val loansScreen = LoansScreen(composeTestRule)
    private val loanModal = LoanModal(composeTestRule)
    private val loanDetailsScreen = LoanDetailsScreen(composeTestRule)
    private val deleteConfirmationModal = DeleteConfirmationModal(composeTestRule)

    @Test
    fun CreateLoan() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.clickAddLoan()
        loanModal.apply {
            enterName("Loan 1")
            enterAmount("4,800.32")
            selectLoanType(LoanType.BORROW)
            colorPicker.chooseColor(Purple2)
            chooseIconFlow.chooseIcon("education")

            clickAdd()
        }

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
    fun EditLoan() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.clickAddLoan()
        loanModal.apply {
            enterName("Razer Blade")
            enterAmount("4,800")
            selectLoanType(LoanType.LEND)
            colorPicker.chooseColor(Blue)
            chooseIconFlow.chooseIcon("star")

            clickAdd()
        }
        loansScreen.assertLoan(
            name = "Razer Blade",
            amount = "4,800",
            amountDecimal = ".00",
            loanType = LoanType.LEND,
            currency = "USD",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
        //--------------- Preparation --------------------------------------------------------------

        //Edit Loan
        loansScreen.clickLoan(
            loanName = "Razer Blade"
        )
        loanDetailsScreen.clickEdit()
        loanModal.apply {
            enterAmount("4,000.25")
            enterName("Laptop")
            chooseIconFlow.chooseIcon("account")
            colorPicker.chooseColor(Ivy)
            selectLoanType(LoanType.BORROW)

            clickSave()
        }

        //Verify edit in LoanDetails
        loanDetailsScreen.apply {
            assertLoanName(loanName = "Laptop")
            assertLoanAmount(
                amount = "4,000",
                amountDecimal = ".25"
            )
            assertAmountPaid(
                amountPaid = "0.00",
                loanAmount = "4,000.25"
            )
            assertPercentPaid(percentPaid = "0.00%")
            assertLeftToPay(leftToPayAmount = "4,000.25")

            clickClose()
        }

        //Verify edit in Loans screen
        loansScreen.assertLoan(
            name = "Laptop",
            loanType = LoanType.BORROW,
            amount = "4,000",
            amountDecimal = ".25",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
    }

    @Test
    fun CreateSeveralLoans() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan 1",
            loanType = LoanType.BORROW,
            color = Purple1,
            icon = "category",
            amount = "0.25"
        )

        loansScreen.addLoanFlow(
            loanName = "Loan 2",
            loanType = LoanType.BORROW,
            color = Blue,
            icon = "education",
            amount = "10,000.00"
        )

        loansScreen.addLoanFlow(
            loanName = "Loan 3",
            loanType = LoanType.LEND,
            color = Purple2,
            icon = "atom",
            amount = "4,235.56"
        )
    }

    @Test
    fun DeleteLoanWithNoRecrods() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan 1",
            loanType = LoanType.BORROW,
            color = Blue2,
            icon = "pet",
            amount = "1,250.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan 1"
        )

        loanDetailsScreen.clickDelete()
        deleteConfirmationModal.confirmDelete()

        loansScreen.assertEmptyState()
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

    //Corner cases
    @Test
    fun OverpayLoan() {

    }
}