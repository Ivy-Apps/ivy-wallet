package com.ivy.domain.usecase.csv

import android.net.Uri
import arrow.core.Either
import com.ivy.base.model.TransactionType
import com.ivy.base.threading.DispatchersProvider
import com.ivy.base.time.TimeConverter
import com.ivy.data.file.FileSystem
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
import org.apache.commons.text.StringEscapeUtils
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
    private val fileSystem: FileSystem,
    private val timeConverter: TimeConverter
) {

    suspend fun exportToFile(
        outputFile: Uri,
        exportScope: suspend TransactionRepository.() -> List<Transaction> = {
            transactionRepository.findAll()
        }
    ): Either<FileSystem.Failure, Unit> = withContext(dispatchers.io) {
        val csv = exportCsv(exportScope)
        fileSystem.writeToFile(outputFile, csv)
    }

    suspend fun exportCsv(
        exportScope: suspend TransactionRepository.() -> List<Transaction>
    ): String = withContext(dispatchers.io) {
        val transactions = transactionRepository.exportScope()
        val accountsMap = accountRepository.findAll().associateBy(Account::id)
        val categoriesMap = categoryRepository.findAll().associateBy(Category::id)

        buildString {
            append(IvyCsvRow.Columns.joinToString(separator = CSV_SEPARATOR))
            append(NEWLINE)
            for (trn in transactions) {
                append(
                    trn.toIvyCsvRow().toCsvString(
                        accountsMap = accountsMap,
                        categoriesMap = categoriesMap
                    )
                )
                append(NEWLINE)
            }
        }
    }

    private fun IvyCsvRow.toCsvString(
        accountsMap: Map<AccountId, Account>,
        categoriesMap: Map<CategoryId, Category>,
    ): String = csvRow {
        // Date
        csvAppend(date?.csvFormat(timeConverter))
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
        csvAppend(dueData?.csvFormat(timeConverter))
        // ID
        csvAppend(id.value.toString())
    }

    @OptIn(ExperimentalTypeInference::class)
    private fun csvRow(@BuilderInference build: CsvRowScope.() -> Unit): String {
        val columns = mutableListOf<String>()
        val rowScope = object : CsvRowScope {
            override fun csvAppend(value: String?) {
                columns.add(value?.escapeCsvString() ?: "")
            }
        }
        rowScope.build()
        return columns.joinToString(separator = CSV_SEPARATOR)
    }

    private fun String.escapeCsvString(): String = try {
        StringEscapeUtils.escapeCsv(this).escapeSpecialChars()
    } catch (e: Exception) {
        escapeSpecialChars()
    }

    private fun String.escapeSpecialChars(): String = replace("\\", "")

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

    private fun Instant.csvFormat(timeConverter: TimeConverter): String {
        return with(timeConverter) {
            this@csvFormat.toLocalDateTime()
        }.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    private fun Double.csvFormat(): String = DecimalFormat(NUMBER_FORMAT).apply {
        decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH)
    }.format(this)

    interface CsvRowScope {
        fun csvAppend(value: String?)
    }

    companion object {
        private const val CSV_SEPARATOR = ","
        private const val NEWLINE = "\n"
        private const val NUMBER_FORMAT = "#,##0.00"
    }
}