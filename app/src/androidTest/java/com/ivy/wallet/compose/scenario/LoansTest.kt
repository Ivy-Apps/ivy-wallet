package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.ui.theme.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class LoansTest : IvyComposeTest() {

    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val loansScreen = LoansScreen(composeTestRule)
    private val loanModal = LoanModal(composeTestRule)
    private val loanDetailsScreen = LoanDetailsScreen(composeTestRule)
    private val loanRecordModal = LoanRecordModal(composeTestRule)

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
            accountsTab.clickAccount(account = "Cash")

            clickAdd()
        }

        loansScreen.assertLoan(
            name = "Loan 1",
            amountLeft = "4,800",
            amountLeftDecimal = ".32",
            loanAmount = "4,800.32",
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
            selectDefaultLoanAccount()

            clickAdd()
        }
        loansScreen.assertLoan(
            name = "Razer Blade",
            amountLeft = "4,800",
            amountLeftDecimal = ".00",
            loanAmount = "4,800.00",
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
            amountLeft = "4,000",
            amountLeftDecimal = ".25",
            loanAmount = "4,000.25",
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
    fun AddLoanRecord() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan 1",
            loanType = LoanType.BORROW,
            amount = "1,000.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan 1"
        )
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("250.50")
            enterNote("Връщам")

            clickAdd()
        }

        loanDetailsScreen.apply {
            assertAmountPaid(
                amountPaid = "250.50",
                loanAmount = "1,000.00"
            )
            assertPercentPaid("25.05%")
            assertLeftToPay("749.50")

            clickLoanRecord(
                amount = "250.50",
                note = "Връщам"
            )

            composeTestRule.onNodeWithTag("modal_close_button")
                .performClick()

            clickClose()
        }

        loansScreen.assertLoan(
            name = "Loan 1",
            loanType = LoanType.BORROW,
            amountLeft = "749",
            amountLeftDecimal = ".50",
            loanAmount = "1,000.00",
            amountPaid = "250.50",
            percentPaid = "25.05"
        )
    }

    @Test
    fun EditLoanRecord() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan",
            loanType = LoanType.LEND,
            amount = "10,000.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan"
        )
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("123.09")

            clickAdd()
        }

        loanDetailsScreen.clickLoanRecord(
            amount = "123.09"
        )
        loanRecordModal.apply {
            enterNote("Cash")
            enterAmount("5,000.00")

            clickSave()
        }

        loanDetailsScreen.apply {
            assertAmountPaid(
                amountPaid = "5,000.00",
                loanAmount = "10,000.00"
            )
            assertPercentPaid("50.00%")
            assertLeftToPay("5,000.00")

            clickLoanRecord(
                amount = "5,000.00",
                note = "Cash"
            )

            clickClose() //click outside of the modal
            clickClose()
        }

        loansScreen.assertLoan(
            name = "Loan",
            loanType = LoanType.LEND,
            amountLeft = "5,000",
            amountLeftDecimal = ".00",
            loanAmount = "10,000.00",
            amountPaid = "5,000.00",
            percentPaid = "50.00"
        )
    }

    @Test
    fun DeleteLoanRecord() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan",
            loanType = LoanType.LEND,
            amount = "1,250.50"
        )

        loansScreen.clickLoan(
            loanName = "Loan"
        )

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("1,053.99")
            clickAdd()
        }
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.clickLoanRecord(
            amount = "1,053.99"
        )
        loanRecordModal.apply {
            clickDelete()
            deleteConfirmationModal.confirmDelete()
        }

        loanDetailsScreen.apply {
            assertAmountPaid(
                amountPaid = "0.00",
                loanAmount = "1,250.50"
            )
            assertPercentPaid("0.00%")
            assertLeftToPay("1,250.50")
            assertNoRecordsEmptyState()

            clickClose()
        }

        loansScreen.assertLoan(
            name = "Loan",
            loanType = LoanType.LEND,
            amountLeft = "1,250",
            amountLeftDecimal = ".50",
            loanAmount = "1,250.50",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
    }

    @Test
    fun AddSeveralLoanRecords() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan",
            loanType = LoanType.LEND,
            amount = "1,000.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan"
        )
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("500")
            enterNote("Initial")
            clickAdd()
        }

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("249.50")
            clickAdd()
        }

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("250.50")
            enterNote("Final payment")
            clickAdd()
        }

        //---------------------------- Assertions --------------------------------------------------
        loanDetailsScreen.apply {
            assertLeftToPay("0.00")
            assertPercentPaid("100.00%")
            assertAmountPaid(
                amountPaid = "1,000.00",
                loanAmount = "1,000.00"
            )
        }
    }

    @Test
    fun DeleteLoanWithRecrods() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan",
            loanType = LoanType.LEND,
            amount = "1,000.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan"
        )

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("500")
            enterNote("Initial")
            clickAdd()
        }
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.clickDelete()
        deleteConfirmationModal.confirmDelete()

        loansScreen.assertEmptyState()
    }

    //Corner cases
    @Test
    fun OverpayLoan() = testWithRetry {
        onboarding.quickOnboarding()

        homeMoreMenu.clickOpenCloseArrow()
        homeMoreMenu.clickLoans()

        loansScreen.addLoanFlow(
            loanName = "Loan",
            loanType = LoanType.BORROW,
            amount = "1,000.00"
        )

        loansScreen.clickLoan(
            loanName = "Loan"
        )
        //-------------------- Preparation ---------------------------------------------------------

        loanDetailsScreen.addRecord()
        loanRecordModal.apply {
            inputAmountOpenModal("2,000.50")
            enterNote("Initial")
            clickAdd()
        }

        //-------------------------- Assertions ----------------------------------------------------
        loanDetailsScreen.apply {
            assertAmountPaid(
                amountPaid = "2,000.50",
                loanAmount = "1,000.00"
            )
            assertPercentPaid("200.05%")
            assertLeftToPay("-1,000.50")

            clickClose()
        }

        loansScreen.assertLoan(
            name = "Loan",
            amountLeft = "-1,000",
            amountLeftDecimal = ".50",
            amountPaid = "2,000.50",
            loanAmount = "1,000.00",
            percentPaid = "200.05",
            loanType = LoanType.BORROW
        )
    }
}