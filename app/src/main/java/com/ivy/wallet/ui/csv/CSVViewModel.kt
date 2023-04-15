package com.ivy.wallet.ui.csv

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.deprecated.logic.csv.IvyFileReader
import com.ivy.wallet.ui.csv.domain.*
import com.opencsv.CSVReaderBuilder
import com.opencsv.validators.LineValidator
import com.opencsv.validators.RowValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.StringReader
import java.nio.charset.Charset
import javax.inject.Inject

@HiltViewModel
class CSVViewModel @Inject constructor(
    private val fileReader: IvyFileReader,
) : ViewModel() {

    private var columns by mutableStateOf<CSVRow?>(null)
    private var csv by mutableStateOf<List<CSVRow>?>(null)

    // region Important fields
    private var amount by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Amount",
            helpInfo = "The amount of the transactions, a positive number. Negative numbers will be made positive.",
            name = "",
            index = -1,
            metadata = 1,
            required = true,
        )
    )
    private var type by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Transaction Type",
            helpInfo = """
                Select the column that determines the transaction type.
                The type of the transaction. Can be Income, Expense or a Transfer.
                If the type is determined by the transaction's amount -> simply select the
                amount column and we'll do our best to match it automatically.
            """.trimIndent(),
            name = "",
            index = -1,
            required = true,
            metadata = TrnTypeMetadata(
                income = "",
                expense = "",
                transfer = null,
            )
        )
    )
    private var date by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Date",
            helpInfo = """
                The date of the transaction. To help us parse it just tell us
                whether the Date or the Month comes first.
            """.trimIndent(),
            name = "",
            index = -1,
            required = true,
            metadata = DateMetadata.DateFirst
        )
    )
    private var account by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Account",
            helpInfo = """
                The account of the transaction.
            """.trimIndent(),
            name = "",
            index = -1,
            required = true,
            metadata = Unit,
        )
    )
    private var accountCurrency by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Account Currency",
            helpInfo = """
                The currency of the account that made the transaction.
                In Ivy Wallet, transactions don't have a currency but inherit
                the ones from their account.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    // endregion

    // region Transfer fields
    private var toAccount by mutableStateOf(
        ColumnMapping(
            ivyColumn = "To Account",
            helpInfo = """
                The account receiving the transfer.
                If you skip it, transfers won't be imported.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    private var toAccountCurrency by mutableStateOf(
        ColumnMapping(
            ivyColumn = "To Account Currency",
            helpInfo = """
                The currency of the account that receives the transfer.
                Skip it if there's no such.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    // endregion

    // region Optional fields
    private var category by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Category",
            helpInfo = """
                The category of the transaction.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    private var title by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Title",
            helpInfo = """
                The title of the transaction.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    private var description by mutableStateOf(
        ColumnMapping(
            ivyColumn = "Description",
            helpInfo = """
                The description of the transaction.
            """.trimIndent(),
            name = "",
            index = -1,
            required = false,
            metadata = Unit,
        )
    )
    // endregion


    @Composable
    fun uiState(): CSVState {
        val sampleCSV = remember(csv) {
            // drop the header
            csv?.drop(1)?.shuffled()?.take(SAMPLE_SIZE)
        }

        val important = importantFields(sampleCSV)
        return CSVState(
            columns = columns,
            csv = csv,
            important = important,
            transfer = transferFields(sampleCSV),
            optional = optionalFields(sampleCSV),
            continueEnabled = continueEnabled(important = important)
        )
    }

    @Composable
    private fun continueEnabled(important: ImportantFields?): Boolean {
        return important != null && important.accountStatus.success &&
                important.amountStatus.success &&
                important.typeStatus.success &&
                important.dateStatus.success
    }

    @Composable
    private fun importantFields(sampleCSV: List<CSVRow>?): ImportantFields? {
        return produceState<ImportantFields?>(
            initialValue = null,
            sampleCSV, amount, type, date, account, accountCurrency,
        ) {
            val result = withContext(Dispatchers.Default) {
                if (sampleCSV != null) {
                    ImportantFields(
                        amount = amount,
                        amountStatus = sampleCSV.parseStatus(amount, ::parseAmount),
                        type = type,
                        typeStatus = sampleCSV.parseStatus(type, ::parseTransactionType),
                        date = date,
                        dateStatus = sampleCSV.parseStatus(date, ::parseDate),
                        account = account,
                        accountStatus = sampleCSV.parseStatus(account, ::parseAccount),
                        accountCurrency = accountCurrency,
                        accountCurrencyStatus = sampleCSV.parseStatus(
                            accountCurrency,
                            ::parseAccountCurrency
                        ),
                    )
                } else null
            }
            value = result
        }.value
    }

    @Composable
    private fun transferFields(sampleCSV: List<CSVRow>?): TransferFields? {
        return produceState<TransferFields?>(
            initialValue = null,
            sampleCSV, toAccount, toAccountCurrency,
        ) {
            val result = withContext(Dispatchers.Default) {
                if (sampleCSV != null) {
                    TransferFields(
                        toAccount = toAccount,
                        toAccountStatus = sampleCSV.parseStatus(toAccount, ::parseToAccount),
                        toAccountCurrency = toAccountCurrency,
                        toAccountCurrencyStatus = sampleCSV.parseStatus(
                            toAccountCurrency,
                            ::parseToAccountCurrency
                        ),
                    )
                } else null
            }
            value = result
        }.value
    }

    @Composable
    private fun optionalFields(sampleCSV: List<CSVRow>?): OptionalFields? {
        return produceState<OptionalFields?>(
            initialValue = null,
            sampleCSV, category, title, description,
        ) {
            val result = withContext(Dispatchers.Default) {
                if (sampleCSV != null) {
                    OptionalFields(
                        category = category,
                        categoryStatus = sampleCSV.parseStatus(category, ::parseCategory),
                        title = title,
                        titleStatus = sampleCSV.parseStatus(title, ::parseTitle),
                        description = description,
                        descriptionStatus = sampleCSV.parseStatus(description, ::parseDescription),
                    )
                } else null
            }
            value = result
        }.value
    }


    private suspend fun handleEvent(event: CSVEvent) {
        when (event) {
            is CSVEvent.FilePicked -> handleFilePicked(event)
            is CSVEvent.AmountMultiplier -> {
                amount = amount.copy(
                    metadata = event.multiplier,
                )
            }
            is CSVEvent.DataMetaChange -> {
                date = date.copy(
                    metadata = event.meta
                )
            }
            is CSVEvent.MapAccount -> {
                account = account.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapAccountCurrency -> {
                accountCurrency = accountCurrency.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapAmount -> {
                amount = amount.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapDate -> {
                date = date.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapType -> {
                type = type.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.TypeMetaChange -> {
                type = type.copy(
                    metadata = event.meta
                )
            }
            is CSVEvent.MapCategory -> {
                category = category.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapDescription -> {
                description = description.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapTitle -> {
                title = title.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapToAccount -> {
                toAccount = toAccount.copy(
                    index = event.index,
                    name = event.name
                )
            }
            is CSVEvent.MapToAccountCurrency -> {
                toAccountCurrency = toAccountCurrency.copy(
                    index = event.index,
                    name = event.name
                )
            }
            CSVEvent.Continue -> handleContinue()
        }
    }

    // region Import CSV
    private suspend fun handleFilePicked(event: CSVEvent.FilePicked) = withContext(Dispatchers.IO) {
        csv = processFile(event.uri)
        columns = csv?.firstOrNull()
    }

    private suspend fun processFile(
        uri: Uri,
        charset: Charset = Charsets.UTF_8
    ): List<CSVRow>? {
        return try {
            val fileContent = fileReader.read(uri, charset) ?: return null
            parseCSV(fileContent).takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            if (charset != Charsets.UTF_16) {
                return processFile(uri, Charsets.UTF_16)
            }
            null
        }
    }

    private suspend fun parseCSV(csv: String): List<CSVRow> {
        val csvReader = CSVReaderBuilder(StringReader(csv))
            .withLineValidator(object : LineValidator {
                override fun isValid(line: String?): Boolean {
                    return true
                }

                override fun validate(line: String?) {
                    //do nothing
                }

            })
            .withRowValidator(object : RowValidator {
                override fun isValid(row: Array<out String>?): Boolean {
                    return true
                }

                override fun validate(row: Array<out String>?) {
                    //do nothing
                }
            })
            .build()

        return csvReader.readAll()
            .map { CSVRow(it.toList()) }
    }
    // endregion

    suspend private fun handleContinue() {

    }


    // region Boiler-plate
    private val events = MutableSharedFlow<CSVEvent>(replay = 0)

    init {
        viewModelScope.launch {
            events.collect {
                handleEvent(it)
            }
        }
    }

    fun onEvent(event: CSVEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }
    // endregion
}