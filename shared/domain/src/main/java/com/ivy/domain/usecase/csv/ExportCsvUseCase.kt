package com.ivy.domain.usecase.csv

import com.ivy.base.model.TransactionType
import com.ivy.base.threading.DispatchersProvider
import com.ivy.base.time.convertToLocal
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.NonNegativeDouble
import com.ivy.data.model.primitive.toNonNegative
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.TransactionRepository
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.experimental.ExperimentalTypeInference

class ExportCsvUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend fun exportCsv(): String = withContext(dispatchers.io) {
        val transactions = transactionRepository.findAll()
        val accountsMap = accountRepository.findAll().associateBy(Account::id)
        val categoriesMap = categoryRepository.findAll().associateBy(Category::id)

        buildString {
            append(IvyCsvRow.Columns.joinToString(separator = CSV_SEPARATOR))
            append('\n')
            for (trn in transactions) {
                val row = trn.toIvyCsvRow().toCsvString(
                    accountsMap = accountsMap,
                    categoriesMap = categoriesMap
                )
                append(row)
                append('\n')
            }
        }
    }

    private fun IvyCsvRow.toCsvString(
        accountsMap: Map<AccountId, Account>,
        categoriesMap: Map<CategoryId, Category>,
    ): String = csvRow {
        // Date
        csvAppend(date?.csvFormat())
        // Title
        csvAppend(title?.value)
        // Category
        csvAppend(categoriesMap[category]?.name?.value)
        // Account
        csvAppend(accountsMap[account]?.name?.value)
        // Amount
        csvAppend(amount.value.csvFormat())
        // Currency
        csvAppend(currency.code)
        // Type
        csvAppend(type.name)
        // Transfer Amount
        csvAppend(transferAmount?.value?.csvFormat())
        // Transfer Currency
        csvAppend(transferCurrency?.code)
        // To Account
        csvAppend(accountsMap[toAccountId]?.name?.value)
        // Receive Amount
        csvAppend(receiveAmount?.value?.csvFormat())
        // Receive Currency
        csvAppend(receiveCurrency?.code)
        // Description
        csvAppend(description?.value)
        // Due Date
        csvAppend(dueData?.csvFormat())
        // ID
        csvAppend(id.value.toString())
    }

    @OptIn(ExperimentalTypeInference::class)
    private fun csvRow(@BuilderInference build: CsvRowScope.() -> Unit): String {
        val row = mutableListOf<String>()
        val rowScope = object : CsvRowScope {
            override fun csvAppend(value: String?) {
                row.add(
                    if (value != null) "$value," else ","
                )
            }
        }
        rowScope.build()
        return row.joinToString(separator = CSV_SEPARATOR)
    }

    private fun Transaction.toIvyCsvRow(): IvyCsvRow = when (this) {
        is Expense -> expenseCsvRow()
        is Income -> incomeCsvRow()
        is Transfer -> transferCsvRow()
    }

    private fun Expense.expenseCsvRow(): IvyCsvRow = IvyCsvRow(
        date = time.takeIf
        { settled },
        title = title,
        category = category,
        account = account,
        amount = value.amount.toNonNegative(),
        currency = value.asset,
        type = TransactionType.EXPENSE,
        transferAmount = null,
        transferCurrency = null,
        toAccountId = null,
        receiveAmount = null,
        receiveCurrency = null,
        description = description,
        dueData = time.takeIf
        { !settled },
        id = id
    )

    private fun Income.incomeCsvRow(): IvyCsvRow = IvyCsvRow(
        date = time.takeIf { settled },
        title = title,
        category = category,
        account = account,
        amount = value.amount.toNonNegative(),
        currency = value.asset,
        type = TransactionType.INCOME,
        transferAmount = null,
        transferCurrency = null,
        toAccountId = null,
        receiveAmount = null,
        receiveCurrency = null,
        description = description,
        dueData = time.takeIf { !settled },
        id = id
    )

    private fun Transfer.transferCsvRow(): IvyCsvRow = IvyCsvRow(
        date = time.takeIf { settled },
        title = title,
        category = category,
        account = fromAccount,
        amount = NonNegativeDouble.unsafe(0.0),
        currency = fromValue.asset,
        type = TransactionType.TRANSFER,
        transferAmount = fromValue.amount,
        transferCurrency = fromValue.asset,
        toAccountId = toAccount,
        receiveAmount = toValue.amount,
        receiveCurrency = toValue.asset,
        description = description,
        dueData = time.takeIf { !settled },
        id = id
    )

    private fun Instant.csvFormat(): String = convertToLocal()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private fun Double.csvFormat(): String = DecimalFormat.getCurrencyInstance(Locale.ENGLISH)
        .format(this)

    companion object {
        private const val CSV_SEPARATOR = ","
    }

    interface CsvRowScope {
        fun csvAppend(value: String?)
    }
}