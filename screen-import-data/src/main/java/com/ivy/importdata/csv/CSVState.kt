package com.ivy.importdata.csv

import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportResult
import kotlinx.collections.immutable.ImmutableList

data class CSVState(
    val uiState: UIState,
    val columns: CSVRow?,
    val csv: ImmutableList<CSVRow>?,
    val important: ImportantFields?,
    val transfer: TransferFields?,
    val optional: OptionalFields?,
    val continueEnabled: Boolean,
)

sealed interface UIState {
    object Idle : UIState
    data class Processing(val percent: Int) : UIState
    data class Result(val importResult: ImportResult) : UIState
}

data class ImportantFields(
    val amount: ColumnMapping<Int>,
    val amountStatus: MappingStatus,
    val type: ColumnMapping<TrnTypeMetadata>,
    val typeStatus: MappingStatus,
    val date: ColumnMapping<DateMetadata>,
    val dateStatus: MappingStatus,
    val account: ColumnMapping<Unit>,
    val accountStatus: MappingStatus,
    val accountCurrency: ColumnMapping<Unit>,
    val accountCurrencyStatus: MappingStatus,
)

data class TrnTypeMetadata(
    val income: String,
    val expense: String,
    val transfer: String?,
)

enum class DateMetadata {
    MonthFirst, DateFirst
}

data class TransferFields(
    val toAccount: ColumnMapping<Unit>,
    val toAccountStatus: MappingStatus,
    val toAccountCurrency: ColumnMapping<Unit>,
    val toAccountCurrencyStatus: MappingStatus,
    val toAmount: ColumnMapping<Int>,
    val toAmountStatus: MappingStatus,
)

data class OptionalFields(
    val category: ColumnMapping<Unit>,
    val categoryStatus: MappingStatus,
    val title: ColumnMapping<Unit>,
    val titleStatus: MappingStatus,
    val description: ColumnMapping<Unit>,
    val descriptionStatus: MappingStatus,
)

data class ColumnMapping<M>(
    val ivyColumn: String,
    val helpInfo: String,
    val name: String,
    val index: Int,
    val metadata: M,
    val required: Boolean,
)

data class MappingStatus(
    val sampleValues: List<String>,
    val success: Boolean,
)

data class CSVRow(
    val values: List<String>
)
