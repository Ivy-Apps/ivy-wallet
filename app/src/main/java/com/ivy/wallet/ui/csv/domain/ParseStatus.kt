package com.ivy.wallet.ui.csv.domain

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.ui.csv.*
import kotlin.math.abs


data class ImportantStatus(
    val amountStatus: MappingStatus,
    val typeStatus: MappingStatus,
    val dateStatus: MappingStatus,
    val accountStatus: MappingStatus,
    val accountCurrencyStatus: MappingStatus,
)

fun parseImportantStatus(
    csv: List<CSVRow>,
    important: ImportantFields,
): ImportantStatus {
    val rows = csv.drop(1).take(10) // drop the header

    return ImportantStatus(
        amountStatus = parseAmountStatus(rows, important.amount),
        typeStatus = parseTypeStatus(rows, important.type),
        dateStatus = mappingFailure(),
        accountStatus = mappingFailure(),
        accountCurrencyStatus = mappingFailure(),
    )
}

private fun parseAmountStatus(
    rows: List<CSVRow>,
    mapping: ColumnMapping<Int>
): MappingStatus = tryStatus {
    val values = rows.values(mapping)
        .mapNotNull {
            val multiplier = when (mapping.metadata) {
                -1000 -> 0.001
                -100 -> 0.01
                -10 -> 0.1
                10 -> 10.0
                100 -> 100.0
                1000 -> 1000.0
                else -> 1.0
            }
            it.toDoubleOrNull()?.times(multiplier)?.let(::abs)
        }

    MappingStatus(
        sampleValues = values.map { it.toString() },
        success = values.size == rows.size
    )
}

private fun parseTypeStatus(
    rows: List<CSVRow>,
    mapping: ColumnMapping<TrnTypeMetadata>
): MappingStatus = tryStatus {
    fun String.tryMeta(metaContains: String): Boolean {
        return metaContains.isNotBlank() &&
                this.contains(metaContains.trim(), ignoreCase = true)
    }

    val values = rows.values(mapping)
        .mapNotNull { value ->
            val meta = mapping.metadata
            with(value) {
                when {
                    tryMeta(meta.expense) -> TransactionType.EXPENSE
                    tryMeta(meta.income) -> TransactionType.INCOME
                    tryMeta(meta.transfer ?: "") -> TransactionType.TRANSFER
                    value.contains("income", ignoreCase = true) -> TransactionType.INCOME
                    value.contains("expense", ignoreCase = true) -> TransactionType.EXPENSE
                    value.contains("transfer", ignoreCase = true) -> TransactionType.TRANSFER
                    else -> null
                }
            }
        }

    MappingStatus(
        sampleValues = values.map { it.toString() },
        success = values.size == rows.size
    )
}


private fun <T> List<CSVRow>.values(mapping: ColumnMapping<T>): List<String> =
    map { it.values[mapping.index] }

private fun tryStatus(block: () -> MappingStatus): MappingStatus = try {
    block()
} catch (e: Exception) {
    MappingStatus(sampleValues = emptyList(), success = false)
}

fun mappingFailure(): MappingStatus = MappingStatus(sampleValues = emptyList(), success = false)