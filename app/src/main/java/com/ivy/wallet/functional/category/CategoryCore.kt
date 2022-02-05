package com.ivy.wallet.functional.category

import arrow.core.NonEmptyList
import com.ivy.wallet.functional.core.ValueFunction
import com.ivy.wallet.functional.core.calculateValueFunctionsSum
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.toFPTransaction
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

suspend fun calculateCategoryValues(
    transactionDao: TransactionDao,
    categoryId: UUID,
    range: ClosedTimeRange,
    valueFunctions: NonEmptyList<ValueFunction<UUID>>
): NonEmptyList<BigDecimal> {
    return calculateCategoryValues(
        categoryId = categoryId,
        retrieveCategoryTransactions = {
            transactionDao.findAllByCategoryAndBetween(
                categoryId = categoryId,
                startDate = range.from,
                endDate = range.to
            )
        },
        valueFunctions = valueFunctions
    )
}

suspend fun calculateCategoryValues(
    categoryId: UUID,
    retrieveCategoryTransactions: suspend (UUID) -> List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<UUID>>
): NonEmptyList<BigDecimal> {
    val categoryTrns = retrieveCategoryTransactions(categoryId)
        .map { it.toFPTransaction() }

    return calculateValueFunctionsSum(
        valueFunctionArgument = categoryId,
        transactions = categoryTrns,
        valueFunctions = valueFunctions
    )
}