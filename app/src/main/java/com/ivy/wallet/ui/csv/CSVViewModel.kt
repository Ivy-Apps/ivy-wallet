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
    private var successPercent by mutableStateOf<Double?>(null)
    private var failedRows by mutableStateOf<List<CSVRow>?>(null)

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
                The type of the transaction. Can be Income, Expense or a Transfer.
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
            required = true,
            metadata = Unit,
        )
    )

    @Composable
    fun uiState(): CSVState {
        return CSVState(
            columns = columns,
            csv = csv,
            important = important(csv),
            transfer = null,
            optional = null,
            successPercent = successPercent,
            failedRows = failedRows,
        )
    }

    @Composable
    private fun important(csv: List<CSVRow>?): ImportantFields? {
        return produceState<ImportantFields?>(
            initialValue = null,
            csv, amount, type, date, account, accountCurrency,
        ) {
            val result = withContext(Dispatchers.Default) {
                if (csv != null) {
                    val sampleRows = csv.drop(1).take(10) // drop the header
                    ImportantFields(
                        amount = amount,
                        amountStatus = sampleRows.parseStatus(amount, ::parseAmount),
                        type = type,
                        typeStatus = sampleRows.parseStatus(type, ::parseTransactionType),
                        date = date,
                        dateStatus = sampleRows.parseStatus(date, ::parseDate),
                        account = account,
                        accountStatus = sampleRows.parseStatus(account, ::parseAccount),
                        accountCurrency = accountCurrency,
                        accountCurrencyStatus = sampleRows.parseStatus(
                            accountCurrency,
                            ::parseAccountCurrency
                        ),
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
        }
    }

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