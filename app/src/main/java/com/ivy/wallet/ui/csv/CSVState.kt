package com.ivy.wallet.ui.csv

import com.ivy.wallet.domain.data.core.Transaction

data class CSVState(
    val columns: CSVRow,
    val csv: List<CSVRow>?,
    val important: ImportantFields?,
    val transfer: TransferFields?,
    val optional: OptionalFields?,
    val transactions: List<Transaction>?
)

data class ImportantFields(
    val amount: ColumnMapping<AmountMetadata>,
    val type: ColumnMapping<TrnTypeMetadata>,
    val date: ColumnMapping<String>,
    val account: ColumnMapping<Unit>,
    val accountCurrency: ColumnMapping<Unit>,
)

sealed interface AmountMetadata {
    data class Multiple(val multiplier: Int) : AmountMetadata
    data class Divider(val divider: Int) : AmountMetadata
}

data class TrnTypeMetadata(
    val income: String,
    val expense: String,
    val transfer: String?,
)

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
    val sampleValues: List<String>,
    val metadata: M,
    val required: Boolean,
    val success: Boolean,
)

data class CSVRow(
    val values: List<String>
)