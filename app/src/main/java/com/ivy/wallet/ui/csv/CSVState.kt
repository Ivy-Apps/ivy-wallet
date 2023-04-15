package com.ivy.wallet.ui.csv

data class CSVState(
    val columns: CSVRow?,
    val csv: List<CSVRow>?,
    val important: ImportantFields?,
    val transfer: TransferFields?,
    val optional: OptionalFields?,

    val successPercent: Double?,
    val failedRows: List<CSVRow>?,
)

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
    val toAccountCurrency: ColumnMapping<Unit>,
)

data class OptionalFields(
    val category: ColumnMapping<Unit>,
    val title: ColumnMapping<Unit>,
    val description: ColumnMapping<Unit>,
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