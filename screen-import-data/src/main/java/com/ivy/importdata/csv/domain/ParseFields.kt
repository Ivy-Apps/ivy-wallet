package com.ivy.importdata.csv.domain

import com.ivy.importdata.csv.CSVRow
import com.ivy.importdata.csv.ColumnMapping
import com.ivy.importdata.csv.DateMetadata
import com.ivy.importdata.csv.TrnTypeMetadata
import com.ivy.base.model.TransactionType
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs

// region Parse amount
fun parseAmount(
    value: String,
    metadata: Int // a broken multiplier
): Double? = tryParse {
    val multiplier = when (metadata) {
        -10_000 -> 0.0001
        -1_000 -> 0.001
        -100 -> 0.01
        -10 -> 0.1
        10 -> 10.0
        100 -> 100.0
        1_000 -> 1_000.0
        1_0000 -> 10_000.0
        else -> 1.0
    }
    val double = parsePositiveDouble(value) ?: return@tryParse null
    abs(double * multiplier)
}

private fun parsePositiveDouble(string: String): Double? {
    val cleanedString = string
        .replace("-", "")
        .replace(" ", ".")
        .filter { it.isDigit() || it == '.' }

    val numberFormat = NumberFormat.getInstance(Locale.US)
    if (numberFormat is DecimalFormat) {
        try {
            numberFormat.applyPattern("#,###.##")
            val parsedNumber = numberFormat.parse(cleanedString)?.toDouble()
            if (parsedNumber != null) {
                return parsedNumber
            }
        } catch (e: Exception) {
            // ignored
        }
    }

    return cleanedString.toDoubleOrNull() ?: string.toDoubleOrNull()
}
// endregion

fun parseTransactionType(
    value: String,
    metadata: TrnTypeMetadata
): TransactionType? {
    fun String.tryMeta(metaContains: String): Boolean {
        return metaContains.isNotBlank() &&
                this.contains(metaContains.trim(), ignoreCase = true)
    }

    return tryParse {
        val cleaned = value.filter { it.isLetterOrDigit() || it == '+' || it == '-' }
        with(cleaned) {
            when {
                tryMeta(metadata.expense) -> TransactionType.EXPENSE
                tryMeta(metadata.income) -> TransactionType.INCOME
                tryMeta(metadata.transfer ?: "") -> TransactionType.TRANSFER
                cleaned.contains("income", ignoreCase = true) -> TransactionType.INCOME
                cleaned.contains("expense", ignoreCase = true) -> TransactionType.EXPENSE
                cleaned.contains("transfer", ignoreCase = true) -> TransactionType.TRANSFER
                cleaned.toDoubleOrNull()?.let { it > 0 } == true -> TransactionType.INCOME
                cleaned.toDoubleOrNull()?.let { it < 0 } == true -> TransactionType.EXPENSE
                else -> null
            }
        }
    }
}

// region Parse Date
var lastSuccessfulFormat: String? = null

fun parseDate(
    value: String,
    metadata: DateMetadata
): LocalDateTime? = tryParse {
    val cleanedValue = value.filter {
        it.isLetterOrDigit() || it == '-' || it == '/' || it == ':' || it == ' '
    }

    val possibleFormats = possibleDateFormats(metadata)
    if (lastSuccessfulFormat != null) {
        try {
            return@tryParse LocalDateTime.parse(
                cleanedValue,
                DateTimeFormatter.ofPattern(lastSuccessfulFormat)
            )
        } catch (e: DateTimeParseException) {
            // Ignore and continue trying other formats
            lastSuccessfulFormat = null
        }
    }
    for (format in possibleFormats) {
        try {
            return@tryParse LocalDateTime.parse(cleanedValue, DateTimeFormatter.ofPattern(format))
        } catch (e: DateTimeParseException) {
            // Ignore and continue trying other formats
        }
    }
    null
}

private fun possibleDateFormats(metadata: DateMetadata): List<String> {
    return when (metadata) {
        DateMetadata.DateFirst -> listOf(
            "dd/MM/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy H:mm",
            "dd-MM-yyyy H:mm",
            "dd/MM/yyyy HH:mm",
            "dd-MM-yyyy HH:mm",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "d MMM yyyy HH:mm:ss",
            "d MMM yyyy H:mm",
            "d MMM yyyy HH:mm",
            "d MMM yyyy",
            "dd MMM yyyy",
            "yyyy/dd/MM HH:mm:ss",
            "yyyy-dd-MM HH:mm:ss",
            "yyyy/dd/MM H:mm",
            "yyyy-dd-MM H:mm",
            "yyyy/dd/MM HH:mm",
            "yyyy-dd-MM HH:mm",
            "yyyy/dd/MM",
            "yyyy-dd-MM",
            "yyyy d MMM HH:mm:ss",
            "yyyy d MMM H:mm",
            "yyyy d MMM HH:mm",
            "yyyy d MMM",
            "yyyy dd MMM"
        )

        DateMetadata.MonthFirst -> listOf(
            "MM/dd/yyyy HH:mm:ss",
            "MM-dd-yyyy HH:mm:ss",
            "MM/dd/yyyy H:mm",
            "MM-dd-yyyy H:mm",
            "MM/dd/yyyy HH:mm",
            "MM-dd-yyyy HH:mm",
            "MM/dd/yyyy",
            "MM-dd-yyyy",
            "MMM d yyyy HH:mm:ss",
            "MMM d yyyy H:mm",
            "MMM d yyyy HH:mm",
            "MMM d yyyy",
            "MMM dd yyyy",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd H:mm",
            "yyyy-MM-dd H:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd",
            "yyyy-MM-dd",
            "yyyy MMM d HH:mm:ss",
            "yyyy MMM d H:mm",
            "yyyy MMM d HH:mm",
            "yyyy MMM d",
            "yyyy MMM dd"
        )
    }
}
// endregion

fun parseAccount(
    value: String,
    metadata: Unit,
): String? = notBlankTrimmedString(value)

fun parseAccountCurrency(
    value: String,
    metadata: Unit,
): String? = notBlankTrimmedString(value)

fun parseToAccount(
    value: String,
    metadata: Unit
): String? = notBlankTrimmedString(value)

fun parseToAccountCurrency(
    value: String,
    metadata: Unit
): String? = notBlankTrimmedString(value)

fun parseCategory(
    value: String,
    metadata: Unit
): String? = notBlankTrimmedString(value)

fun parseTitle(
    value: String,
    metadata: Unit
): String? = notBlankTrimmedString(value)

fun parseDescription(
    value: String,
    metadata: Unit
): String? = notBlankTrimmedString(value)

fun <M> CSVRow.extractValue(
    mapping: ColumnMapping<M>
): String {
    return try {
        values[mapping.index]
    } catch (e: Exception) {
        ""
    }
}

// region Util
private fun notBlankTrimmedString(value: String): String? =
    value.trim().takeIf { it.isNotBlank() }

private fun <T> tryParse(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}
// endregion
