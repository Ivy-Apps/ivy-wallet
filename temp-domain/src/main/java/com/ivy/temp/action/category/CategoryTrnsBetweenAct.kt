package com.ivy.wallet.domain.action.category

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
import javax.inject.Inject

class CategoryTrnsBetweenAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<CategoryTrnsBetweenAct.Input, List<TransactionOld>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionOld> = suspend {
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
        val between: com.ivy.base.ClosedTimeRange
    )
}

fun actInput(
    categoryId: UUID,
    between: com.ivy.base.ClosedTimeRange
) = CategoryTrnsBetweenAct.Input(
    categoryId = categoryId,
    between = between
)