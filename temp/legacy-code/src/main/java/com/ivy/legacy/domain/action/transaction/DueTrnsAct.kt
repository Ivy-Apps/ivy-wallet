package com.ivy.wallet.domain.action.transaction

import com.ivy.data.model.Transaction
import com.ivy.data.repository.TransactionRepository
import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import javax.inject.Inject

class DueTrnsAct @Inject constructor(
    private val transactionRepository: TransactionRepository
) : FPAction<ClosedTimeRange, List<Transaction>>() {

    override suspend fun ClosedTimeRange.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionRepository.findAllDueToBetween(
                startDate = from,
                endDate = to
            )
        }
    }
}
