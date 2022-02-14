package com.ivy.wallet.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.base.computationThread
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.*
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.uploader.TransactionUploader
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import java.time.LocalDateTime
import java.util.*


data class LoanTransactionsLogic(
    val Loan: GeneralLoanTransactionsLogic.LoanSealedClass,
    val LoanRecord: GeneralLoanTransactionsLogic.LoanRecordSealedClass
) {
    suspend fun updateAssociatedLoanData(transaction: Transaction?) {
        computationThread {

            if (transaction == null)
                return@computationThread

            if (transaction.loanId != null && transaction.loanRecordId == null) {
                Loan.updateAssociatedLoan(transaction)
            } else if (transaction.loanId != null && transaction.loanRecordId != null) {
                LoanRecord.updateAssociatedLoanRecord(
                    transaction = transaction
                )
            }
        }
    }
}

sealed class GeneralLoanTransactionsLogic(
    private val categoryDao: CategoryDao,
    private val transactionUploader: TransactionUploader,
    private val transactionDao: TransactionDao,
    private val ivyContext: IvyContext,
    private val loanRecordDao: LoanRecordDao,
    private val loanDao: LoanDao,
    private val settingsDao: SettingsDao,
    private val accountsDao: AccountDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
) {

    class LoanSealedClass(
        val categoryDao: CategoryDao,
        val transactionUploader: TransactionUploader,
        val transactionDao: TransactionDao,
        val ivyContext: IvyContext,
        val loanRecordDao: LoanRecordDao,
        val loanDao: LoanDao,
        val settingsDao: SettingsDao,
        val accountsDao: AccountDao,
        val exchangeRatesLogic: ExchangeRatesLogic
    ) : GeneralLoanTransactionsLogic(
        categoryDao,
        transactionUploader,
        transactionDao,
        ivyContext,
        loanRecordDao,
        loanDao,
        settingsDao,
        accountsDao,
        exchangeRatesLogic
    ) {
        suspend fun editAssociatedLoanTransaction(
            loan: Loan,
            createLoanTransaction: Boolean = false,
            transaction: Transaction?
        ) {
            computationThread {
                updateAssociatedTransaction(
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

        suspend fun createAssociatedLoanTransaction(data: CreateLoanData, loanId: UUID) {
            computationThread {
                updateAssociatedTransaction(
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

        suspend fun deleteAssociatedLoanTransactions(loanId: UUID) {
            deleteAssociatedTransactions(loanId = loanId)
        }

        suspend fun recalculateLoanRecords(
            oldLoan: Loan,
            newLoan: Loan,
            defaultCurrencyCode: String,
            accounts: List<Account>
        ) {
            computationThread {
                val oldLoanAccount = findAccount(accounts, oldLoan.accountId)
                val newLoanAccount = findAccount(accounts, newLoan.accountId)

                if (oldLoan.accountId == newLoan.accountId || oldLoanAccount?.currency == newLoanAccount?.currency)
                    return@computationThread

                calculateLoanRecords(
                    loanId = newLoan.id,
                    newAccountId = newLoan.accountId,
                    defaultCurrencyCode = defaultCurrencyCode,
                    accounts = accounts
                )
            }
        }

        suspend fun updateAssociatedLoan(transaction: Transaction?) {
            computationThread {
                transaction?.loanId ?: return@computationThread
                val accountsList = ioThread { accountsDao.findAll() }

                calculateLoanRecords(
                    loanId = transaction.loanId,
                    newAccountId = transaction.accountId,
                    defaultCurrencyCode = baseCurrency(),
                    accounts = accountsList
                )

                val loan =
                    ioThread { loanDao.findById(transaction.loanId) } ?: return@computationThread

                val modifiedLoan = loan.copy(
                    amount = transaction.amount,
                    name = transaction.title ?: loan.name,
                    type = if (transaction.type == TransactionType.INCOME) LoanType.BORROW else LoanType.LEND,
                    accountId = transaction.accountId
                )
                ioThread {
                    loanDao.save(modifiedLoan)
                }
            }
        }
    }

    class LoanRecordSealedClass(
        val categoryDao: CategoryDao,
        val transactionUploader: TransactionUploader,
        val transactionDao: TransactionDao,
        val ivyContext: IvyContext,
        val loanRecordDao: LoanRecordDao,
        val loanDao: LoanDao,
        val settingsDao: SettingsDao,
        val accountsDao: AccountDao,
        val exchangeRatesLogic: ExchangeRatesLogic
    ) : GeneralLoanTransactionsLogic(
        categoryDao,
        transactionUploader,
        transactionDao,
        ivyContext,
        loanRecordDao,
        loanDao,
        settingsDao,
        accountsDao,
        exchangeRatesLogic
    ) {
        suspend fun editAssociatedLoanTransaction(
            loan: Loan,
            createLoanRecordTransaction: Boolean,
            loanRecord: LoanRecord,
        ) {
            computationThread {
                val transaction =
                    ioThread { transactionDao.findLoanRecordTransaction(loanRecord.id) }
                updateAssociatedTransaction(
                    createTransaction = createLoanRecordTransaction,
                    loanRecordId = loanRecord.id,
                    loanId = loan.id,
                    amount = loanRecord.amount,
                    loanType = loan.type,
                    selectedAccountId = loanRecord.accountId,
                    title = loanRecord.note,
                    time = loanRecord.dateTime,
                    isLoanRecord = true,
                    transaction = transaction
                )
            }
        }

        suspend fun createAssociatedLoanTransaction(
            data: CreateLoanRecordData,
            loan: Loan,
            loanRecordId: UUID
        ) {
            computationThread {
                updateAssociatedTransaction(
                    createTransaction = data.createLoanRecordTransaction,
                    loanType = loan.type,
                    amount = data.amount,
                    title = data.note,
                    time = data.dateTime,
                    loanRecordId = loanRecordId,
                    loanId = loan.id,
                    selectedAccountId = data.account?.id,
                    isLoanRecord = true,
                )
            }
        }

        suspend fun deleteAssociatedLoanRecordTransaction(loanRecordId: UUID) {
            deleteAssociatedTransactions(loanRecordId = loanRecordId)
        }

        suspend fun updateAssociatedLoanRecord(
            transaction: Transaction?
        ) {
            transaction?.loanId ?: return
            transaction.loanRecordId ?: return
            computationThread {
                val loanRecord = ioThread { loanRecordDao.findById(transaction.loanRecordId) }
                val loan = ioThread { loanDao.findById(transaction.loanId) }
                val accountsList = ioThread { accountsDao.findAll() }

                val loanCurrency =
                    findAccount(accountsList, loan?.accountId)?.currency ?: baseCurrency()
                val newCurrency =
                    findAccount(accountsList, transaction.accountId)?.currency ?: baseCurrency()


                val convertedAmount =
                    if (newCurrency == loanCurrency) null else exchangeRatesLogic.convertAmount(
                        baseCurrency = baseCurrency(),
                        amount = transaction.amount,
                        fromCurrency = newCurrency,
                        toCurrency = loanCurrency
                    )

                val modifiedLoanRecord = loanRecord?.let { fetchedRecord ->
                    fetchedRecord.copy(
                        amount = transaction.amount,
                        note = transaction.title,
                        dateTime = transaction.dateTime ?: fetchedRecord.dateTime,
                        accountId = transaction.accountId,
                        convertedAmount = convertedAmount
                    )
                }
                modifiedLoanRecord?.let {
                    loanRecordDao.save(it)
                }
            }
        }
    }


    protected suspend fun updateAssociatedTransaction(
        createTransaction: Boolean,
        loanRecordId: UUID? = null,
        loanId: UUID,
        amount: Double,
        loanType: LoanType,
        selectedAccountId: UUID?,
        title: String? = null,
        category: Category? = null,
        time: LocalDateTime? = null,
        isLoanRecord: Boolean = false,
        transaction: Transaction? = null,
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

    protected suspend fun calculateLoanRecords(
        newAccountId: UUID?,
        loanId: UUID,
        defaultCurrencyCode: String,
        accounts: List<Account>
    ) {
        computationThread {
            val newCurrency =
                findAccount(accounts, newAccountId)?.currency ?: defaultCurrencyCode

            val loanRecords = ioThread {
                loanRecordDao.findAllByLoanId(loanId).map { loanRecord ->
                    val convertedAmount: Double? =
                        if (loanRecord.accountId == newAccountId) null else
                            exchangeRatesLogic.convertAmount(
                                baseCurrency = defaultCurrencyCode,
                                amount = loanRecord.amount,
                                fromCurrency = findAccount(accounts, loanRecord.accountId)?.currency
                                    ?: defaultCurrencyCode,
                                toCurrency = newCurrency
                            )
                    loanRecord.copy(convertedAmount = convertedAmount)
                }
            }
            ioThread {
                loanRecordDao.save(loanRecords)
            }
        }
    }

    protected suspend fun deleteAssociatedTransactions(
        loanId: UUID? = null,
        loanRecordId: UUID? = null
    ) {
        if (loanId == null && loanRecordId == null)
            return

        ioThread {
            val transactions: List<Transaction?> =
                if (loanId != null) transactionDao.findAllByLoanId(loanId = loanId) else
                    listOf(transactionDao.findLoanRecordTransaction(loanRecordId!!))

            transactions.forEach { trans ->
                deleteTransaction(trans)
            }
        }
    }

    protected fun findAccount(
        accounts: List<Account>,
        accountId: UUID?,
    ): Account? {
        return accountId?.let { uuid ->
            accounts.find { acc ->
                acc.id == uuid
            }
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
        transaction: Transaction? = null
    ) {
        if (selectedAccountId == null)
            return

        val transType = if (isLoanRecord)
            if (loanType == LoanType.BORROW) TransactionType.EXPENSE else TransactionType.INCOME
        else
            if (loanType == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE

        val transCategoryId: UUID? = getCategoryId(existingCategoryId = categoryId)

        val modifiedTransaction: Transaction = transaction?.copy(
            loanId = loanId,
            loanRecordId = if (isLoanRecord) loanRecordId else null,
            amount = amount,
            type = transType,
            accountId = selectedAccountId,
            title = title,
            categoryId = transCategoryId,
            dateTime = time
        )
            ?: Transaction(
                accountId = selectedAccountId,
                type = transType,
                amount = amount,
                dateTime = time,
                categoryId = transCategoryId,
                title = title,
                loanId = loanId,
                loanRecordId = if (isLoanRecord) loanRecordId else null
            )

        ioThread {
            transactionDao.save(modifiedTransaction)
        }
    }

    private suspend fun deleteTransaction(transaction: Transaction?) {
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
            categoryDao.findAll()
        }

        var addCategoryToDb = false

        val loanCategory = categoryList.find { category ->
            category.name.lowercase(Locale.ENGLISH).contains("loan")
        } ?: if (ivyContext.isPremium || categoryList.size < 12) {
            addCategoryToDb = true
            Category(
                "Loans",
                color = IVY_COLOR_PICKER_COLORS_FREE[4].toArgb(),
                icon = "loan"
            )
        } else null

        if (addCategoryToDb)
            ioThread {
                loanCategory?.let {
                    categoryDao.save(it)
                }
            }

        return loanCategory?.id
    }

    protected suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }
}

