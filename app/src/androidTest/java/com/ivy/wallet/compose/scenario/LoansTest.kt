package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.loan.LoanDetailsScreen
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.ui.theme.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class LoansTest : IvyComposeTest() {

    @Test
    fun CreateLoan() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .clickAddLoan()
            .enterName("Loan 1")
            .enterAmount("4,800.32")
            .selectLoanType(LoanType.BORROW)
            .chooseColor(Purple2)
            .chooseIcon("education")
            .selectDefaultLoanAccount()
            .clickAdd()
            .assertLoan(
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
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .clickAddLoan()
            .enterName("Razer Blade")
            .enterAmount("4,800")
            .selectLoanType(LoanType.LEND)
            .chooseColor(Blue)
            .chooseIcon("star")
            .selectDefaultLoanAccount()
            .clickAdd()
            .assertLoan(
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
            .clickLoan(
                loanName = "Razer Blade"
            )
            .clickEdit()
            .enterAmount("4,000.25")
            .enterName("Laptop")
            .chooseIcon("account")
            .chooseColor(Ivy)
            .selectLoanType(LoanType.BORROW)

            .clickSave(next = LoanDetailsScreen(composeTestRule))


            //Verify edit in LoanDetails
            .assertLoanName(loanName = "Laptop")
            .assertLoanAmount(
                amount = "4,000",
                amountDecimal = ".25"
            )
            .assertAmountPaid(
                amountPaid = "0.00",
                loanAmount = "4,000.25"
            )
            .assertPercentPaid(percentPaid = "0.00%")
            .assertLeftToPay(leftToPayAmount = "4,000.25")
            .clickClose()
            //Verify edit in Loans screen
            .assertLoan(
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
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan 1",
                loanType = LoanType.BORROW,
                color = Purple1,
                icon = "category",
                amount = "0.25"
            )
            .addLoanFlow(
                loanName = "Loan 2",
                loanType = LoanType.BORROW,
                color = Blue,
                icon = "education",
                amount = "10,000.00"
            )
            .addLoanFlow(
                loanName = "Loan 3",
                loanType = LoanType.LEND,
                color = Purple2,
                icon = "atom",
                amount = "4,235.56"
            )
    }

    @Test
    fun DeleteLoanWithNoRecrods() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan 1",
                loanType = LoanType.BORROW,
                color = Blue2,
                icon = "pet",
                amount = "1,250.00"
            )
            .clickLoan(
                loanName = "Loan 1"
            )
            .deleteWithConfirmation()
            .assertEmptyState()
    }

    //Loan records ---------------------------------------------------------------------------------
    @Test
    fun AddLoanRecord() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan 1",
                loanType = LoanType.BORROW,
                amount = "1,000.00"
            )
            .clickLoan(
                loanName = "Loan 1"
            )
            //-------------------- Preparation ---------------------------------------------------------
            .addRecord()
            .enterAmountWhenAmountInputOpened("250.50")
            .enterNote("Връщам")
            .clickAdd()
            .assertAmountPaid(
                amountPaid = "250.50",
                loanAmount = "1,000.00"
            )
            .assertPercentPaid("25.05%")
            .assertLeftToPay("749.50")

            .clickLoanRecord(
                amount = "250.50",
                note = "Връщам"
            )
            .clickClose()
            .clickClose()
            .assertLoan(
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
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan",
                loanType = LoanType.LEND,
                amount = "10,000.00"
            )
            .clickLoan(
                loanName = "Loan"
            )
            //-------------------- Preparation ---------------------------------------------------------
            .addRecord()
            .enterAmountWhenAmountInputOpened("123.09")
            .clickAdd()
            .clickLoanRecord(
                amount = "123.09"
            )
            .enterNote("Cash")
            .enterAmount("5,000.00")
            .clickSave()
            .assertAmountPaid(
                amountPaid = "5,000.00",
                loanAmount = "10,000.00"
            )
            .assertPercentPaid("50.00%")
            .assertLeftToPay("5,000.00")
            .clickLoanRecord(
                amount = "5,000.00",
                note = "Cash"
            )
            .clickClose() //click outside of the modal
            .clickClose()
            .assertLoan(
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
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan",
                loanType = LoanType.LEND,
                amount = "1,250.50"
            )
            .clickLoan(
                loanName = "Loan"
            )
            .addRecord()
            .enterAmountWhenAmountInputOpened("1,053.99")
            .clickAdd()
            //-------------------- Preparation ---------------------------------------------------------
            .clickLoanRecord(
                amount = "1,053.99"
            )
            .deleteWithConfirmation()
            .assertAmountPaid(
                amountPaid = "0.00",
                loanAmount = "1,250.50"
            )
            .assertPercentPaid("0.00%")
            .assertLeftToPay("1,250.50")
            .assertNoRecordsEmptyState()
            .clickClose()
            .assertLoan(
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
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan",
                loanType = LoanType.LEND,
                amount = "1,000.00"
            )
            .clickLoan(
                loanName = "Loan"
            )
            //-------------------- Preparation ---------------------------------------------------------
            .addRecord()
            .enterAmountWhenAmountInputOpened("500")
            .enterNote("Initial")
            .clickAdd()
            .addRecord()
            .enterAmountWhenAmountInputOpened("249.50")
            .clickAdd()

            .addRecord()
            .enterAmountWhenAmountInputOpened("250.50")
            .enterNote("Final payment")
            .clickAdd()

            //---------------------------- Assertions --------------------------------------------------
            .assertLeftToPay("0.00")
            .assertPercentPaid("100.00%")
            .assertAmountPaid(
                amountPaid = "1,000.00",
                loanAmount = "1,000.00"
            )
    }

    @Test
    fun DeleteLoanWithRecrods() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan",
                loanType = LoanType.LEND,
                amount = "1,000.00"
            )
            .clickLoan(
                loanName = "Loan"
            )
            .addRecord()
            .enterAmountWhenAmountInputOpened("500")
            .enterNote("Initial")
            .clickAdd()
            //-------------------- Preparation ---------------------------------------------------------
            .deleteWithConfirmation()
            .assertEmptyState()
    }

    //Corner cases
    @Test
    fun OverpayLoan() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickLoans()
            .addLoanFlow(
                loanName = "Loan",
                loanType = LoanType.BORROW,
                amount = "1,000.00"
            )
            .clickLoan(
                loanName = "Loan"
            )
            //-------------------- Preparation ---------------------------------------------------------
            .addRecord()
            .enterAmountWhenAmountInputOpened("2,000.50")
            .enterNote("Initial")
            .clickAdd()
            //-------------------------- Assertions ----------------------------------------------------
            .assertAmountPaid(
                amountPaid = "2,000.50",
                loanAmount = "1,000.00"
            )
            .assertPercentPaid("200.05%")
            .assertLeftToPay("-1,000.50")
            .clickClose()
            .assertLoan(
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