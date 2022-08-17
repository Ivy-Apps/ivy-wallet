package com.ivy.wallet.domain.deprecated.logic.loantrasactions

import androidx.compose.ui.graphics.toArgb
import com.ivy.base.IVY_COLOR_PICKER_COLORS_FREE
import com.ivy.base.R
import com.ivy.common.timeNowUTC
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.loan.Loan
import com.ivy.data.loan.LoanRecord
import com.ivy.data.loan.LoanType
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.sync.uploader.TransactionUploader
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.computationThread
import com.ivy.wallet.utils.ioThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class LoanTransactionsCore(
    private val categoryDao: CategoryDao,
    private val transactionUploader: TransactionUploader,
    private val transactionDao: TransactionDao,
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val loanRecordDao: LoanRecordDao,
    private val loanDao: LoanDao,
    private val settingsDao: SettingsDao,
    private val accountsDao: AccountDao,
    private val exchangeRatesLogic: ExchangeRatesLogic
) {
    private var baseCurrencyCode: String? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            baseCurrencyCode = baseCurrency()
        }
    }

    suspend fun deleteAssociatedTransactions(
        loanId: UUID? = null,
        loanRecordId: UUID? = null
    ) {
        if (loanId == null && loanRecordId == null)
            return

        ioThread {
            val transactions: List<TransactionOld?> =
                if (loanId != null) transactionDao.findAllByLoanId(loanId = loanId)
                    .map { it.toDomain() } else
                    listOf(transactionDao.findLoanRecordTransaction(loanRecordId!!)).map { it?.toDomain() }

            transactions.forEach { trans ->
                deleteTransaction(trans)
            }
        }
    }

    fun findAccount(
        accounts: List<AccountOld>,
        accountId: UUID?,
    ): AccountOld? {
        return accountId?.let { uuid ->
            accounts.find { acc ->
                acc.id == uuid
            }
        }
    }

    suspend fun baseCurrency(): String =
        ioThread { baseCurrencyCode ?: settingsDao.findFirst().currency }


    suspend fun updateAssociatedTransaction(
        createTransaction: Boolean,
        loanRecordId: UUID? = null,
        loanId: UUID,
        amount: Double,
        loanType: LoanType,
        selectedAccountId: UUID?,
        title: String? = null,
        category: CategoryOld? = null,
        time: LocalDateTime? = null,
        isLoanRecord: Boolean = false,
        transaction: TransactionOld? = null,
    ) {
        if (isLoanRecord && loanRecordId == null)
            return

        if (createTransaction && transaction != null) {
            createMainTransaction(
                loanRecordId = loanRecordId,
                loanId = loanId,
                amount = amount,
                loanType = loanType,
                selectedAccountId = selectedAccountId,
                title = title ?: transaction.title,
                categoryId = category?.id ?: transaction.categoryId,
                time = time ?: transaction.dateTime ?: timeNowUTC(),
                isLoanRecord = isLoanRecord,
                transaction = transaction
            )
        } else if (createTransaction && transaction == null) {
            createMainTransaction(
                loanRecordId = loanRecordId,
                loanId = loanId,
                amount = amount,
                loanType = loanType,
                selectedAccountId = selectedAccountId,
                title = title,
                categoryId = category?.id,
                time = time ?: timeNowUTC(),
                isLoanRecord = isLoanRecord,
                transaction = transaction
            )
        } else {
            deleteTransaction(transaction = transaction)
        }
    }

    private suspend fun createMainTransaction(
        loanRecordId: UUID? = null,
        amount: Double,
        loanType: LoanType,
        loanId: UUID,
        selectedAccountId: UUID?,
        title: String? = null,
        categoryId: UUID? = null,
        time: LocalDateTime = timeNowUTC(),
        isLoanRecord: Boolean = false,
        transaction: TransactionOld? = null
    ) {
        if (selectedAccountId == null)
            return

        val transType = if (isLoanRecord)
            if (loanType == LoanType.BORROW) TrnType.EXPENSE else TrnType.INCOME
        else
            if (loanType == LoanType.BORROW) TrnType.INCOME else TrnType.EXPENSE

        val transCategoryId: UUID? = getCategoryId(existingCategoryId = categoryId)

        val modifiedTransaction: TransactionOld = transaction?.copy(
            loanId = loanId,
            loanRecordId = if (isLoanRecord) loanRecordId else null,
            amount = amount.toBigDecimal(),
            type = transType,
            accountId = selectedAccountId,
            title = title,
            categoryId = transCategoryId,
            dateTime = time
        )
            ?: TransactionOld(
                accountId = selectedAccountId,
                type = transType,
                amount = amount.toBigDecimal(),
                dateTime = time,
                categoryId = transCategoryId,
                title = title,
                loanId = loanId,
                loanRecordId = if (isLoanRecord) loanRecordId else null
            )

        ioThread {
            transactionDao.save(modifiedTransaction.toEntity())
        }
    }

    private suspend fun deleteTransaction(transaction: TransactionOld?) {
        ioThread {
            transaction?.let {
                transactionDao.flagDeleted(it.id)
            }

            transaction?.let {
                transactionUploader.delete(it.id)
            }
        }
    }

    private suspend fun getCategoryId(existingCategoryId: UUID? = null): UUID? {
        if (existingCategoryId != null)
            return existingCategoryId

        val categoryList = ioThread {
            categoryDao.findAll().map { it.toDomain() }
        }

        var addCategoryToDb = false

        val loanCategory = categoryList.find { category ->
            category.name.lowercase(Locale.ENGLISH).contains("loan")
        } ?: if (ivyContext.isPremium || categoryList.size < 12) {
            addCategoryToDb = true
            CategoryOld(
                com.ivy.core.ui.temp.stringRes(R.string.loans),
                color = IVY_COLOR_PICKER_COLORS_FREE[4].toArgb(),
                icon = "loan"
            )
        } else null

        if (addCategoryToDb)
            ioThread {
                loanCategory?.let {
                    categoryDao.save(it.toEntity())
                }
            }

        return loanCategory?.id
    }

    suspend fun computeConvertedAmount(
        oldLoanRecordAccountId: UUID?,
        oldLonRecordConvertedAmount: Double?,
        oldLoanRecordAmount: Double,
        newLoanRecordAccountID: UUID?,
        newLoanRecordAmount: Double,
        loanAccountId: UUID?,
        accounts: List<AccountOld>,
        reCalculateLoanAmount: Boolean = false,
    ): Double? {
        return computationThread {

            val newLoanRecordCurrency =
                newLoanRecordAccountID.fetchAssociatedCurrencyCode(accountsList = accounts)

            val oldLoanRecordCurrency =
                oldLoanRecordAccountId.fetchAssociatedCurrencyCode(accountsList = accounts)

            val loanCurrency = loanAccountId.fetchAssociatedCurrencyCode(accountsList = accounts)

            val loanRecordCurrenciesChanged = oldLoanRecordCurrency != newLoanRecordCurrency

            val newConverted: Double? = when {
                newLoanRecordCurrency == loanCurrency -> {
                    null
                }

                reCalculateLoanAmount || loanRecordCurrenciesChanged
                        || oldLonRecordConvertedAmount == null -> {
                    ioThread {
                        exchangeRatesLogic.convertAmount(
                            baseCurrency = baseCurrency(),
                            amount = newLoanRecordAmount,
                            fromCurrency = newLoanRecordCurrency,
                            toCurrency = loanCurrency
                        )
                    }
                }

                oldLoanRecordAmount != newLoanRecordAmount -> {
                    newLoanRecordAmount * (oldLonRecordConvertedAmount / oldLoanRecordAmount)
                }

                else -> {
                    oldLonRecordConvertedAmount
                }
            }
            newConverted
        }
    }

    private suspend fun UUID?.fetchAssociatedCurrencyCode(accountsList: List<AccountOld>): String {
        return findAccount(accountsList, this)?.currency ?: baseCurrency()
    }

    suspend fun fetchAccounts() = ioThread {
        accountsDao.findAll()
    }

    suspend fun saveLoanRecords(loanRecords: List<LoanRecord>) = ioThread {
        loanRecordDao.save(loanRecords.map { it.toEntity() })
    }

    suspend fun saveLoanRecords(loanRecord: LoanRecord) = ioThread {
        loanRecordDao.save(loanRecord.toEntity())
    }

    suspend fun saveLoan(loan: Loan) = ioThread {
        loanDao.save(loan.toEntity())
    }

    suspend fun fetchLoanRecord(loanRecordId: UUID) = ioThread {
        loanRecordDao.findById(loanRecordId)
    }

    suspend fun fetchAllLoanRecords(loanId: UUID) = ioThread {
        loanRecordDao.findAllByLoanId(loanId)
    }

    suspend fun fetchLoan(loanId: UUID) = ioThread {
        loanDao.findById(loanId)
    }

    suspend fun fetchLoanRecordTransaction(loanRecordId: UUID?): TransactionOld? {
        return loanRecordId?.let {
            ioThread {
                transactionDao.findLoanRecordTransaction(it)?.toDomain()
            }
        }
    }
}