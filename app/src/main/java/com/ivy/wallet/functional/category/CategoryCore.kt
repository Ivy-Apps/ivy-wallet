package com.ivy.wallet.functional.category

import arrow.core.NonEmptyList
import com.ivy.wallet.functional.core.calculateValueFunctionsSumSuspend
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.toFPTransaction
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

suspend fun calculateCategoryValues(
    transactionDao: TransactionDao,
    accountDao: AccountDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
    valueFunctions: NonEmptyList<CategoryValueFunction>
): NonEmptyList<BigDecimal> {
    return calculateCategoryValues(
        argument = CategoryValueFunctions.Argument(
            categoryId = categoryId,
            accounts = accountDao.findAll(),
            exchangeRateDao = exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode
        ),
        retrieveCategoryTransactions = {
            if (it == null) {
                transactionDao.findAllUnspecifiedAndBetween(
                    startDate = range.from,
                    endDate = range.to
                )
            } else {
                transactionDao.findAllByCategoryAndBetween(
                    categoryId = it,
                    startDate = range.from,
                    endDate = range.to
                )
            }

        },
        valueFunctions = valueFunctions
    )
}

suspend fun calculateCategoryValues(
    argument: CategoryValueFunctions.Argument,
    retrieveCategoryTransactions: suspend (UUID?) -> List<Transaction>,
    valueFunctions: NonEmptyList<CategoryValueFunction>
): NonEmptyList<BigDecimal> {
    val categoryTrns = retrieveCategoryTransactions(argument.categoryId)
        .map { it.toFPTransaction() }

    return calculateValueFunctionsSumSuspend(
        valueFunctionArgument = argument,
        transactions = categoryTrns,
        valueFunctions = valueFunctions
    )
}