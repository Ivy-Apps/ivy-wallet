package com.ivy.wallet.domain.pure.category

import arrow.core.NonEmptyList
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.pure.core.calculateValueFunctionsSumSuspend
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.domain.pure.data.toFPTransaction
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

suspend fun calculateCategoryValuesWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
    accountIdFilterSet: Set<UUID>,
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
                ).filter {
                    if (accountIdFilterSet.isNotEmpty())
                        accountIdFilterSet.contains(it.accountId)
                    else
                        true
                }
            } else {
                walletDAOs.transactionDao.findAllByCategoryAndBetween(
                    categoryId = forCategoryId,
                    startDate = range.from,
                    endDate = range.to
                ).filter {
                    if (accountIdFilterSet.isNotEmpty())
                        accountIdFilterSet.contains(it.accountId)
                    else
                        true
                }
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