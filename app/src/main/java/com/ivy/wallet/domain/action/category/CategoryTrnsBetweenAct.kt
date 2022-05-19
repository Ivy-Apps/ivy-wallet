package com.ivy.wallet.domain.action.category

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
import javax.inject.Inject

class CategoryTrnsBetweenAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<CategoryTrnsBetweenAct.Input, List<Transaction>>() {

    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionDao.findAllByCategoryAndBetween(
                startDate = between.from,
                endDate = between.to,
                categoryId = categoryId
            )
        }
    } thenMap { it.toDomain() }

    data class Input(
        val categoryId: UUID,
        val between: ClosedTimeRange
    )
}

fun actInput(
    categoryId: UUID,
    between: ClosedTimeRange
) = CategoryTrnsBetweenAct.Input(
    categoryId = categoryId,
    between = between
)