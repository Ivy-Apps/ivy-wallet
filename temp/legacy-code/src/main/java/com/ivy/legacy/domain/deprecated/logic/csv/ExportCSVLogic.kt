package com.ivy.wallet.domain.deprecated.logic.csv

import android.content.Context
import android.net.Uri
import com.ivy.base.legacy.writeToFile
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.repository.TransactionRepository
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.formatLocal
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.localDecimalSeparator
import org.apache.commons.text.StringEscapeUtils
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class ExportCSVLogic @Inject constructor(
    private val settingsDao: SettingsDao,
    private val transactionRepository: TransactionRepository,
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
            transactionRepository.findAll()
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
        csv.appendValue(time.atZone(ZoneId.systemDefault()).toLocalDateTime()) {
            append(it.formatLocal(CSV_DATETIME_FORMAT))
        }

        // Title
        csv.appendValue(title?.value) {
            append(it.escapeCSVString())
        }

        // Category
        csv.appendValue(category?.value) {
            append(categoryMap[it]?.name?.escapeCSVString() ?: it)
        }

        when (this) {
            is Expense -> {
                expenseCSV(accountMap, baseCurrency, csv, categoryMap)
            }
            is Income -> {
                incomeCSV(accountMap, baseCurrency, csv, categoryMap)
            }
            is Transfer -> {
                transferCSV(accountMap, baseCurrency, csv, categoryMap)
            }
        }
        return csv.toString()
    }

    private fun Transfer.transferCSV(
        accountMap: Map<UUID, Account>,
        baseCurrency: String,
        csv: StringBuilder,
        categoryMap: Map<UUID, Category>
    ) {
        val account = accountMap[this.fromAccount.value]
        val currency = account?.currency ?: baseCurrency
        // Account
        csv.appendValue(account) {
            append(account?.name?.escapeCSVString() ?: it)
        }

        // Type
        csv.appendValue(javaClass.simpleName) {
            append(it)
        }
        // Transfer Amount
        csv.appendValue(fromValue.amount.value) {
            append(it.formatAmountCSV(currency))
        }

        // Transfer Currency
        csv.appendValue(currency) {
            append(it)
        }

        // To Account
        csv.appendValue(toAccount.value) {
            append(accountMap[it]?.name?.escapeCSVString() ?: it)
        }
        val receiveCurrency = toAccount.value.let { accountMap[it]?.currency ?: baseCurrency }
        // Receive Amount
        csv.appendValue(toValue.amount.value) {
            append(it.formatAmountCSV(receiveCurrency))
        }

        // Receive Currency
        csv.appendValue(receiveCurrency) {
            append(it)
        }
        // Description
        csv.appendValue(description?.value) {
            append(it.escapeCSVString())
        }
        // Due Date
        csv.appendValue(time.atZone(ZoneId.systemDefault()).toLocalDateTime()) {
            append(it.formatLocal(CSV_DATETIME_FORMAT))
        }
        // ID
        csv.appendValue(id) {
            append(it)
        }
        // Account Color
        csv.appendValue(account?.color) {
            append(it)
        }

        // Account orderNum
        csv.appendValue(account?.orderNum) {
            append(it)
        }

        // Category Color
        csv.appendValue(category?.value?.let { categoryMap[it]?.color }) {
            append(it)
        }

        // Category orderNum
        csv.appendValue(category?.value?.let { categoryMap[it]?.orderNum }) {
            append(it)
        }

        // To Account Color
        csv.appendValue(toAccount.value.let { accountMap[it]?.color }) {
            append(it)
        }

        // To Account orderNum
        csv.appendValue(toAccount.value.let { accountMap[it]?.orderNum }) {
            append(it)
        }

        // Account Icon
        csv.appendValue(account?.icon) {
            append(it)
        }

        // Category Icon
        csv.appendValue(category?.value?.let { categoryMap[it]?.icon }) {
            append(it)
        }

        // To Account Icon
        csv.appendValue(toAccount.value.let { accountMap[it]?.icon }) {
            append(it)
        }
    }

    private fun Income.incomeCSV(
        accountMap: Map<UUID, Account>,
        baseCurrency: String,
        csv: StringBuilder,
        categoryMap: Map<UUID, Category>
    ) {
        // Account
        val account = accountMap[this.account.value]
        val currency = account?.currency ?: baseCurrency
        csv.appendValue(account) {
            append(account?.name?.escapeCSVString() ?: it)
        }
        // Amount
        csv.appendValue(value.amount.value) {
            append(it.formatAmountCSV(currency))
        }
        // Currency
        csv.appendValue(currency) {
            append(it)
        }

        // Type
        csv.appendValue(javaClass.simpleName) {
            append(it)
        }
        // Description
        csv.appendValue(description?.value) {
            append(it.escapeCSVString())
        }
        // ID
        csv.appendValue(id) {
            append(it)
        }
        // Account Color
        csv.appendValue(account?.color) {
            append(it)
        }

        // Account orderNum
        csv.appendValue(account?.orderNum) {
            append(it)
        }

        // Category Color
        csv.appendValue(category?.value?.let { categoryMap[it]?.color }) {
            append(it)
        }

        // Category orderNum
        csv.appendValue(category?.value?.let { categoryMap[it]?.orderNum }) {
            append(it)
        }
        // Account Icon
        csv.appendValue(account?.icon) {
            append(it)
        }

        // Category Icon
        csv.appendValue(category?.value?.let { categoryMap[it]?.icon }) {
            append(it)
        }
    }

    private fun Expense.expenseCSV(
        accountMap: Map<UUID, Account>,
        baseCurrency: String,
        csv: StringBuilder,
        categoryMap: Map<UUID, Category>
    ) {
        val account = accountMap[this.account.value]
        val currency = account?.currency ?: baseCurrency

        // Account
        csv.appendValue(account) {
            append(account?.name?.escapeCSVString() ?: it)
        }
        // Amount
        csv.appendValue(value.amount.value) {
            append((-it).formatAmountCSV(currency))
        }
        // Currency
        csv.appendValue(currency) {
            append(it)
        }

        // Type
        csv.appendValue(javaClass.simpleName) {
            append(it)
        }
        // Description
        csv.appendValue(description?.value) {
            append(it.escapeCSVString())
        }
        // ID
        csv.appendValue(id) {
            append(it)
        }
        // Account Color
        csv.appendValue(account?.color) {
            append(it)
        }

        // Account orderNum
        csv.appendValue(account?.orderNum) {
            append(it)
        }

        // Category Color
        csv.appendValue(category?.value?.let { categoryMap[it]?.color }) {
            append(it)
        }

        // Category orderNum
        csv.appendValue(category?.value?.let { categoryMap[it]?.orderNum }) {
            append(it)
        }

        // Account Icon
        csv.appendValue(account?.icon) {
            append(it)
        }

        // Category Icon
        csv.appendValue(category?.value?.let { categoryMap[it]?.icon }) {
            append(it)
        }
    }

    private fun Double.formatAmountCSV(currency: String): String {
        val ivyAmountFormat = format(currency)

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
