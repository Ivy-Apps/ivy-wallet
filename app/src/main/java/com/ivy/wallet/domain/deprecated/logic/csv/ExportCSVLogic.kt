package com.ivy.wallet.domain.deprecated.logic.csv

import android.content.Context
import android.net.Uri
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.formatLocal
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.localDecimalSeparator
import com.ivy.wallet.utils.writeToFile
import org.apache.commons.text.StringEscapeUtils
import java.util.UUID
import javax.inject.Inject

class ExportCSVLogic @Inject constructor(
    private val settingsDao: SettingsDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) {
    companion object {
        private const val COMMA = ","
        private const val CSV_DATETIME_FORMAT = "dd/MM/yyyy HH:mm"
    }

    suspend fun exportToFile(
        context: Context,
        fileUri: Uri,
        exportScope: suspend () -> List<Transaction> = {
            transactionDao.findAll().map { it.toDomain() }
        }
    ) {
        val csv = generateCSV(
            exportScope = exportScope
        )

        ioThread {
            writeToFile(
                context = context,
                uri = fileUri,
                content = csv
            )
        }
    }

    private suspend fun generateCSV(
        exportScope: suspend () -> List<Transaction>
    ): String {
        return ioThread {
            val accountMap = accountDao
                .findAll()
                .map { it.id to it }
                .toMap()
            val categoryMap = categoryDao
                .findAll()
                .map { it.id to it }
                .toMap()

            val baseCurrency = settingsDao.findFirst().currency
            val csvRows = exportScope()
                .joinToString("\n") {
                    it.toCSV(
                        baseCurrency = baseCurrency,
                        accountMap = accountMap.mapValues { it.value.toDomain() },
                        categoryMap = categoryMap.mapValues { it.value.toDomain() }
                    )
                }

            "Date, Title, Category, Account, Amount, Currency, Type, " +
                "Transfer Amount, Transfer Currency, To Account, Receive Amount, Receive Currency, " +
                "Description, Due Date, ID, Account Color, " +
                "Account orderNum, Category Color, Category orderNum, " +
                "To Account Color, To Account orderNum, Account Icon, Category Icon, To Account Icon\n" +
                csvRows
        }
    }

    private fun Transaction.toCSV(
        baseCurrency: String,
        accountMap: Map<UUID, Account>,
        categoryMap: Map<UUID, Category>
    ): String {
        val csv = StringBuilder()

        // Date
        csv.appendValue(dateTime) {
            append(it.formatLocal(CSV_DATETIME_FORMAT))
        }

        // Title
        csv.appendValue(title) {
            append(it.escapeCSVString())
        }

        // Category
        csv.appendValue(categoryId) {
            append(categoryMap[it]?.name?.escapeCSVString() ?: it)
        }

        val account = accountMap[accountId]
        val currency = account?.currency ?: baseCurrency

        // Account
        csv.appendValue(accountId) {
            append(account?.name?.escapeCSVString() ?: it)
        }

        // Amount
        csv.appendValue(amount) {
            val amountFormatted = when (type) {
                TransactionType.INCOME -> it
                TransactionType.EXPENSE -> -it
                TransactionType.TRANSFER -> 0.0
            }.toDouble().formatAmountCSV(currency)
            append(amountFormatted)
        }

        // Currency
        csv.appendValue(currency) {
            append(it)
        }

        // Type
        csv.appendValue(type) {
            append(it.name)
        }

        // Transfer Amount
        csv.appendValue(if (type == TransactionType.TRANSFER) amount else null) {
            append(it.toDouble().formatAmountCSV(currency))
        }

        // Transfer Currency
        csv.appendValue(if (type == TransactionType.TRANSFER) currency else null) {
            append(it)
        }

        // To Account
        csv.appendValue(toAccountId) {
            append(accountMap[it]?.name?.escapeCSVString() ?: it)
        }

        val receiveCurrency = toAccountId?.let { accountMap[it]?.currency ?: baseCurrency }
        // Receive Amount
        csv.appendValue(toAmount) {
            append(it.toDouble().formatAmountCSV(receiveCurrency ?: baseCurrency))
        }

        // Receive Currency
        csv.appendValue(receiveCurrency) {
            append(it)
        }

        // Description
        csv.appendValue(description) {
            append(it.escapeCSVString())
        }

        // Due Date
        csv.appendValue(dueDate) {
            append(it.formatLocal(CSV_DATETIME_FORMAT))
        }

        // ID
        csv.appendValue(id) {
            append(it)
        }

        // Account Color
        csv.appendValue(accountMap[accountId]?.color) {
            append(it)
        }

        // Account orderNum
        csv.appendValue(accountMap[accountId]?.orderNum) {
            append(it)
        }

        // Category Color
        csv.appendValue(categoryId?.let { categoryMap[it]?.color }) {
            append(it)
        }

        // Category orderNum
        csv.appendValue(categoryId?.let { categoryMap[it]?.orderNum }) {
            append(it)
        }

        // To Account Color
        csv.appendValue(toAccountId?.let { accountMap[it]?.color }) {
            append(it)
        }

        // To Account orderNum
        csv.appendValue(toAccountId?.let { accountMap[it]?.orderNum }) {
            append(it)
        }

        // Account Icon
        csv.appendValue(accountMap[accountId]?.icon) {
            append(it)
        }

        // Category Icon
        csv.appendValue(categoryId?.let { categoryMap[it]?.icon }) {
            append(it)
        }

        // To Account Icon
        csv.appendValue(toAccountId?.let { accountMap[it]?.icon }) {
            append(it)
        }

        return csv.toString()
    }

    private fun Double.formatAmountCSV(currency: String): String {
        val ivyAmountFormat = this.format(currency)

        // string result example: 1078.38
        return when (localDecimalSeparator()) {
            "." -> {
                // source string example: 1,078.38
                ivyAmountFormat
                    .replace(",", "")
            }
            "," -> {
                // source string example: 1.078,39
                ivyAmountFormat
                    .replace(".", "")
                    .replace(",", ".")
            }
            else -> ivyAmountFormat
        }.escapeCSVString()
    }

    private fun String.escapeCSVString(): String {
        return try {
            StringEscapeUtils.escapeCsv(this)
        } catch (e: Exception) {
            e.printStackTrace()
            this.replace(",", " ")
                .replace("\n", " ")
        }
    }

    private fun <T> StringBuilder.appendValue(value: T?, appendNonNull: StringBuilder.(T) -> Unit) {
        if (value != null) {
            appendNonNull(value)
        }
        append(COMMA)
    }
}
