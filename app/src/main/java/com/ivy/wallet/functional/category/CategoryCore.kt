package com.ivy.wallet.functional.category

import arrow.core.NonEmptyList
import com.ivy.wallet.functional.core.calculateValueFunctionsSumSuspend
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.WalletDAOs
import com.ivy.wallet.functional.data.toFPTransaction
import com.ivy.wallet.model.entity.Transaction
import java.math.BigDecimal
import java.util.*

suspend fun calculateCategoryValues(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
    valueFunctions: NonEmptyList<CategoryValueFunction>
): NonEmptyList<BigDecimal> {
    return calculateCategoryValues(
        argument = CategoryValueFunctions.Argument(
            categoryId = categoryId,
            accounts = walletDAOs.accountDao.findAll(),
            exchangeRateDao = walletDAOs.exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode
        ),
        retrieveCategoryTransactions = { forCategoryId ->
            if (forCategoryId == null) {
                walletDAOs.transactionDao.findAllUnspecifiedAndBetween(
                    startDate = range.from,
                    endDate = range.to
                )
            } else {
                walletDAOs.transactionDao.findAllByCategoryAndBetween(
                    categoryId = forCategoryId,
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