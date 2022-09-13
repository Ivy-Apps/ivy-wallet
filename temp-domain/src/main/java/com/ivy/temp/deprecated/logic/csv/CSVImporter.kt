package com.ivy.wallet.domain.deprecated.logic.csv

import androidx.compose.ui.graphics.toArgb
import com.ivy.base.IVY_COLOR_PICKER_COLORS_FREE
import com.ivy.common.convertLocalToUTC
import com.ivy.common.timeNowUTC
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.IvyCurrency
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.design.l0_system.Green
import com.ivy.design.l0_system.IvyDark
import com.ivy.wallet.domain.deprecated.logic.csv.model.CSVRow
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportResult
import com.ivy.wallet.domain.deprecated.logic.csv.model.RowMapping
import com.ivy.wallet.domain.pure.util.nextOrderNum
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.toLowerCaseLocal
import com.opencsv.CSVReaderBuilder
import com.opencsv.validators.LineValidator
import com.opencsv.validators.RowValidator
import timber.log.Timber
import java.io.StringReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue

class CSVImporter(
    private val settingsDao: SettingsDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {

    lateinit var accounts: List<AccountOld>
    lateinit var categories: List<CategoryOld>

    private var newCategoryColorIndex = 0
    private var newAccountColorIndex = 0

    suspend fun import(
        csv: String,
        rowMapping: RowMapping,
        onProgress: suspend (progressPercent: Double) -> Unit,
    ): ImportResult {
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

        val rows = csvReader.readAll()
            .map { it.toList() }
        val rowsCount = rows.size

        newCategoryColorIndex = 0
        newAccountColorIndex = 0

        accounts = accountDao.findAllSuspend().map { it.toDomain() }
        val initialAccountsCount = accounts.size

        categories = categoryDao.findAllSuspend().map { it.toDomain() }
        val initialCategoriesCount = categories.size

        val baseCurrency = settingsDao.findFirstSuspend().currency

        val failedRows = mutableListOf<CSVRow>()

        // Some importers require postprocessing transactions
        // Example: Financisto separates in and out transfers
        val joinResult = rowMapping.joinTransactions(
            rows.mapIndexedNotNull { index, row ->
                val progressPercent = if (rowsCount > 0)
                    index / rowsCount.toDouble() else 0.0
                onProgress(progressPercent / 2)

                val transaction = mapToTransaction(
                    baseCurrency = baseCurrency,
                    row = row,
                    rowMapping = rowMapping
                )

                if (transaction == null) {
                    failedRows.add(
                        CSVRow(
                            index = index + 2, //+ 1 because we skip Header and +1 because they don't start from zero
                            content = row
                        )
                    )
                }
                transaction
            }
        )

        val transactions = joinResult.transactions

        for ((index, transaction) in transactions.withIndex()) {
            val progressPercent = if (rowsCount > 0)
                index / transactions.size.toDouble() else 0.0
            onProgress(0.5 + progressPercent / 2)
            transactionDao.save(transaction.toEntity())
        }

        return ImportResult(
            rowsFound = rowsCount - joinResult.mergedCount,
            transactionsImported = transactions.size,
            accountsImported = accounts.size - initialAccountsCount,
            categoriesImported = categories.size - initialCategoriesCount,
            failedRows = failedRows
        )
    }

    private suspend fun mapToTransaction(
        baseCurrency: String,
        row: List<String>,
        rowMapping: RowMapping
    ): TransactionOld? {
        val type = mapType(
            row = row,
            rowMapping = rowMapping
        ) ?: return null

        val toAccount = if (type == TrnTypeOld.TRANSFER) {
            mapAccount(
                baseCurrency = baseCurrency,
                accountNameString = row.extract(rowMapping.toAccount),
                currencyRawString = row.extract(rowMapping.toAccountCurrency),
                color = row.extract(rowMapping.toAccountColor)?.toIntOrNull(),
                icon = row.extract(rowMapping.toAccountIcon),
                orderNum = row.extract(rowMapping.toAccountOrderNum)?.toDoubleOrNull()
            )
        } else null

        val csvAmount = if (type != TrnTypeOld.TRANSFER) {
            mapAmount(row.extract(rowMapping.amount))
        } else {
            mapAmount(row.extract(rowMapping.transferAmount))
        } ?: return null
        val amount = csvAmount.absoluteValue

        if (amount <= 0) {
            //Cannot save transactions with zero amount
            return null
        }

        val toAmount = if (type == TrnTypeOld.TRANSFER) {
            mapAmount(row.extract(rowMapping.toAmount))
        } else null


        val dateTime = mapDate(
            rowMapping = rowMapping,
            dateString = if (rowMapping.timeOnly != null) {
                // date and time are separated in csv, join them with space
                row.extract(rowMapping.date) + " " + row.extract(rowMapping.timeOnly)
            } else {
                row.extract(rowMapping.date)
            }
        )
        val dueDate = mapDate(
            rowMapping = rowMapping,
            dateString = row.extract(rowMapping.dueDate)
        )
        if (dateTime == null && dueDate == null) {
            //Cannot save transactions without any date
            return null
        }

        val account = mapAccount(
            baseCurrency = baseCurrency,
            accountNameString = row.extract(rowMapping.account),
            currencyRawString = row.extract(rowMapping.accountCurrency),
            color = row.extract(rowMapping.accountColor)?.toIntOrNull(),
            icon = row.extract(rowMapping.accountIcon),
            orderNum = row.extract(rowMapping.accountOrderNum)?.toDoubleOrNull()
        ) ?: return null

        val category = mapCategory(
            categoryNameString = row.extract(rowMapping.category),
            color = row.extract(rowMapping.categoryColor)?.toIntOrNull(),
            icon = row.extract(rowMapping.categoryIcon),
            orderNum = row.extract(rowMapping.categoryOrderNum)?.toDoubleOrNull()
        )
        val title = row.extract(rowMapping.title)
        val description = row.extract(rowMapping.description)
        val id = mapId(row.extract(rowMapping.id))


        return rowMapping.transformTransaction(
            TransactionOld(
                id = id,
                type = type,
                amount = amount.toBigDecimal(),
                accountId = account.id,
                toAccountId = toAccount?.id,
                toAmount = toAmount?.toBigDecimal() ?: amount.toBigDecimal(),
                dateTime = dateTime,
                dueDate = dueDate,
                categoryId = category?.id,
                title = title,
                description = description
            ),
            category,
            csvAmount
        )
    }

    private fun mapType(
        row: List<String>,
        rowMapping: RowMapping
    ): TrnTypeOld? {
        //Return Expense for intentionally set Type mapping to null
        //Example: Fortune City
        if (rowMapping.type == null) return TrnTypeOld.EXPENSE

        val type = row.extract(rowMapping.type) ?: return null
        // default is expense as some apps only declare transfers
        if (type.isBlank()) return TrnTypeOld.EXPENSE

        val normalizedType = type.toLowerCaseLocal()

        return when {
            normalizedType.contains("income") -> TrnTypeOld.INCOME
            normalizedType.contains("expense") -> TrnTypeOld.EXPENSE
            normalizedType.contains("transfer") -> TrnTypeOld.TRANSFER
            else -> {
                // Default to Expense because Financisto messed up its CSV Export
                // and mixes it with another (ignored) column
                if (rowMapping.defaultTypeToExpense) TrnTypeOld.EXPENSE else null
            }
        }
    }

    private fun mapAmount(amount: String?): Double? {
        if (amount == null || amount.isBlank()) return null

        return amount
            .replace(",", "")
            .toDoubleOrNull()
    }

    private fun mapDate(
        rowMapping: RowMapping,
        dateString: String?
    ): LocalDateTime? {
        if (dateString == null || dateString.isBlank()) return null

        if (rowMapping.dateOnlyFormat != null) {
            try {
                return dateString.parseDateOnly(rowMapping.dateOnlyFormat)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (rowMapping.dateTimeFormat != null) {
            try {
                return dateString.parseDateTime(rowMapping.dateTimeFormat)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val supportedPatterns = listOf(
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-dd-MM HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",
            "dd/MM/yyyy h:mm a",

            //Fortune City Date variations with 24-h
            "d/M/yyyy HH:mm",
            "d/MM/yyyy HH:mm",
            "dd/M/yyyy HH:mm",
            "dd/MM/yyyy HH:mm",
            "M/d/yyyy HH:mm",
            "M/dd/yyyy HH:mm",
            "MM/d/yyyy HH:mm",
            "MM/dd/yyyy HH:mm",

            //Fortune City Date variations with 12-h (am/pm)
            "d/M/yyyy h:mm a",
            "d/MM/yyyy h:mm a",
            "dd/M/yyyy h:mm a",
            "dd/MM/yyyy h:mm a",
            "M/d/yyyy h:mm a",
            "M/dd/yyyy h:mm a",
            "MM/d/yyyy h:mm a",
            "MM/dd/yyyy h:mm a",

            //Fortune City Date with "-" variations with 24-h
            "d-M-yyyy HH:mm",
            "d-MM-yyyy HH:mm",
            "dd-M-yyyy HH:mm",
            "dd-MM-yyyy HH:mm",
            "M-d-yyyy HH:mm",
            "M-dd-yyyy HH:mm",
            "MM-d-yyyy HH:mm",
            "MM-dd-yyyy HH:mm",

            //Fortune City Date with "-" variations with 12-h (am/pm)
            "d-M-yyyy h:mm a",
            "d-MM-yyyy h:mm a",
            "dd-M-yyyy h:mm a",
            "dd-MM-yyyy h:mm a",
            "M-d-yyyy h:mm a",
            "M-dd-yyyy h:mm a",
            "MM-d-yyyy h:mm a",
            "MM-dd-yyyy h:mm a",

            //More Fortune City Date formats
            "dd-MM-yy H:mm",
            "dd-MM-yy HH:mm",
            "MM-dd-yy H:mm",
            "MM-dd-yy HH:mm",
        )

        for (pattern in supportedPatterns) {
            try {
                return dateString.parseDateTime(dateTimeFormat = pattern)
            } catch (e: Exception) {
            }
        }

        val supportedDateOnlyPatterns = listOf(
            "MM/dd/yyyy",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd/MM/yyyy"
        )

        for (pattern in supportedDateOnlyPatterns) {
            try {
                return dateString.parseDateOnly(dateFormat = pattern)
            } catch (e: Exception) {
            }
        }

        val isoFormats = listOf(
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.RFC_1123_DATE_TIME
        )

        for (format in isoFormats) {
            try {
                val parsedDate = LocalDateTime.parse(dateString, format)
                if (parsedDate != null) {
                    return parsedDate
                }
            } catch (e: Exception) {
            }
        }

        Timber.e("Import: Cannot parse $dateString")
        //As a fallback set all transactions 1 year before now
        return timeNowUTC()
            .minusYears(1)
    }

    private fun String.parseDateOnly(
        dateFormat: String
    ): LocalDateTime {
        return LocalDate.parse(
            this,
            DateTimeFormatter.ofPattern(dateFormat)
        ).atTime(12, 0).convertLocalToUTC()
    }

    private fun String.parseDateTime(
        dateTimeFormat: String
    ): LocalDateTime {
        return LocalDateTime.parse(
            this,
            DateTimeFormatter.ofPattern(dateTimeFormat)
        ).convertLocalToUTC()
    }

    private suspend fun mapAccount(
        baseCurrency: String,
        accountNameString: String?,
        color: Int?,
        icon: String?,
        orderNum: Double?,
        currencyRawString: String?,
    ): AccountOld? {
        if (accountNameString == null || accountNameString.isBlank()) return null

        val existingAccount = accounts.firstOrNull {
            accountNameString.toLowerCaseLocal() == it.name.toLowerCaseLocal()
        }
        if (existingAccount != null) {
            return existingAccount
        }

        //create new account
        val colorArgb = color ?: when {
            accountNameString.toLowerCaseLocal().contains("cash") -> {
                Green
            }
            accountNameString.toLowerCaseLocal().contains("revolut") -> {
                IvyDark
            }
            else -> IVY_COLOR_PICKER_COLORS_FREE.getOrElse(newAccountColorIndex++) {
                newAccountColorIndex = 0
                IVY_COLOR_PICKER_COLORS_FREE.first()
            }
        }.toArgb()

        val newAccount = AccountOld(
            name = accountNameString,
            currency = mapCurrency(
                baseCurrency = baseCurrency,
                currencyCode = currencyRawString
            ),
            color = colorArgb,
            icon = icon,
            orderNum = orderNum ?: accountDao.findMaxOrderNum().nextOrderNum()
        )
        accountDao.save(newAccount.toEntity())
        accounts = accountDao.findAllSuspend().map { it.toDomain() }

        return newAccount
    }

    private fun mapCurrency(
        baseCurrency: String,
        currencyCode: String?
    ): String {
        return try {
            if (currencyCode != null && currencyCode.isNotBlank()) {
                IvyCurrency.fromCode(currencyCode)?.code ?: baseCurrency
            } else {
                baseCurrency
            }
        } catch (e: Exception) {
            baseCurrency
        }

    }

    private suspend fun mapCategory(
        categoryNameString: String?,
        color: Int?,
        icon: String?,
        orderNum: Double?
    ): CategoryOld? {
        if (categoryNameString == null || categoryNameString.isBlank()) return null

        val existingCategory = categories.firstOrNull {
            categoryNameString.toLowerCaseLocal() == it.name.toLowerCaseLocal()
        }
        if (existingCategory != null) {
            return existingCategory
        }

        //create new category
        val colorArgb = color ?: IVY_COLOR_PICKER_COLORS_FREE.getOrElse(newCategoryColorIndex++) {
            newCategoryColorIndex = 0
            IVY_COLOR_PICKER_COLORS_FREE.first()
        }.toArgb()

        val newCategory = CategoryOld(
            name = categoryNameString,
            color = colorArgb,
            icon = icon,
            orderNum = orderNum ?: categoryDao.findMaxOrderNum().nextOrderNum()
        )
        categoryDao.save(newCategory.toEntity())
        categories = categoryDao.findAllSuspend().map { it.toDomain() }

        return newCategory
    }

    private fun mapId(idString: String?): UUID {
        return try {
            if (idString.isNullOrBlank()) UUID.randomUUID()

            UUID.fromString(idString)
        } catch (e: Exception) {
            UUID.randomUUID()
        }
    }

    private fun List<String>.extract(index: Int?): String? =
        index?.let { this.getOrNull(index) }
}