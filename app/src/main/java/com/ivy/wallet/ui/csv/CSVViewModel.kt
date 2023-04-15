package com.ivy.wallet.ui.csv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.deprecated.logic.csv.CSVNormalizer
import com.ivy.wallet.domain.deprecated.logic.csv.IvyFileReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.validators.LineValidator
import com.opencsv.validators.RowValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.StringReader
import javax.inject.Inject

@HiltViewModel
class CSVViewModel @Inject constructor(
    private val fileReader: IvyFileReader,
) : ViewModel() {

    private var columns by mutableStateOf<CSVRow?>(null)
    private var csv by mutableStateOf<List<CSVRow>?>(null)
    private var important by mutableStateOf<ImportantFields?>(null)
    private var transfer by mutableStateOf<TransferFields?>(null)
    private var optional by mutableStateOf<OptionalFields?>(null)
    private var successPercent by mutableStateOf<Double?>(null)
    private var failedRows by mutableStateOf<List<CSVRow>?>(null)

    @Composable
    fun uiState(): CSVState = CSVState(
        columns = columns,
        csv = csv,
        important = important,
        transfer = transfer,
        optional = optional,
        successPercent = successPercent,
        failedRows = failedRows,
    )


    private suspend fun handleEvent(event: CSVEvent) {
        when (event) {
            is CSVEvent.FilePicked -> handleFilePicked(event)
            is CSVEvent.AmountMultiplier -> TODO()
            is CSVEvent.DataMetaChange -> TODO()
            is CSVEvent.MapAccount -> TODO()
            is CSVEvent.MapAccountCurrency -> TODO()
            is CSVEvent.MapAmount -> TODO()
            is CSVEvent.MapDate -> TODO()
            is CSVEvent.MapType -> TODO()
            is CSVEvent.TypeMetaChange -> TODO()
        }
    }

    private suspend fun handleFilePicked(event: CSVEvent.FilePicked) = withContext(Dispatchers.IO) {
        val fileContent = fileReader.read(event.uri, Charsets.UTF_8) ?: return@withContext
        csv = parseCSV(fileContent).takeIf { it.isNotEmpty() }
    }

    private suspend fun parseCSV(csv: String): List<CSVRow> {
        val csvReader = CSVReaderBuilder(StringReader(csv))
            .withSkipLines(1)
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