package com.ivy.wallet.logic.loantrasactions

import com.ivy.wallet.base.computationThread
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.model.entity.LoanRecord
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.uploader.TransactionUploader
import com.ivy.wallet.ui.IvyContext
import java.util.*

class LTLoanMapper(
    private val ltCore: LoanTransactionsCore
) {

    suspend fun createAssociatedLoanTransaction(data: CreateLoanData, loanId: UUID) {
        computationThread {
            ltCore.updateAssociatedTransaction(
                createTransaction = data.createLoanTransaction,
                loanId = loanId,
                amount = data.amount,
                loanType = data.type,
                selectedAccountId = data.account?.id,
                title = data.name,
                isLoanRecord = false,
            )
        }
    }

    suspend fun editAssociatedLoanTransaction(
        loan: Loan,
        createLoanTransaction: Boolean = false,
        transaction: Transaction?
    ) {
        computationThread {
            ltCore.updateAssociatedTransaction(
                createTransaction = createLoanTransaction,
                loanId = loan.id,
                amount = loan.amount,
                loanType = loan.type,
                selectedAccountId = loan.accountId,
                title = loan.name,
                isLoanRecord = false,
                transaction = transaction,
                time = transaction?.dateTime
            )
        }
    }


    suspend fun deleteAssociatedLoanTransactions(loanId: UUID) {
        ltCore.deleteAssociatedTransactions(loanId = loanId)
    }

    suspend fun recalculateLoanRecords(
        oldLoan: Loan,
        newLoan: Loan
    ) {
        val accounts = ltCore.fetchAccounts()

        computationThread {
            val oldLoanAccount = ltCore.findAccount(accounts, oldLoan.accountId)
            val newLoanAccount = ltCore.findAccount(accounts, newLoan.accountId)

            if (oldLoan.accountId == newLoan.accountId || oldLoanAccount?.currency == newLoanAccount?.currency)
                return@computationThread

            val newLoanRecords = ltCore.calculateLoanRecords(
                loanId = newLoan.id,
                newAccountId = newLoan.accountId,
            )

            ltCore.saveLoanRecords(newLoanRecords)
        }
    }

    suspend fun updateAssociatedLoan(
        transaction: Transaction?,
        onBackgroundProcessingStart: suspend () -> Unit = {},
        onBackgroundProcessingEnd: suspend () -> Unit = {},
    ) {
        computationThread {
            transaction?.loanId ?: return@computationThread

            onBackgroundProcessingStart()

            val loan = ltCore.fetchLoan(transaction.loanId) ?: return@computationThread
            val newLoanRecords: List<LoanRecord> = ltCore.calculateLoanRecords(
                loanId = transaction.loanId,
                newAccountId = transaction.accountId
            )

            val modifiedLoan = loan.copy(
                amount = transaction.amount,
                name = transaction.title ?: loan.name,
                type = if (transaction.type == TransactionType.INCOME) LoanType.BORROW else LoanType.LEND,
                accountId = transaction.accountId
            )

            ltCore.saveLoanRecords(newLoanRecords)
            ltCore.saveLoan(modifiedLoan)
        }
        onBackgroundProcessingEnd()
    }
}
