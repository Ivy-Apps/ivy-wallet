package com.ivy.wallet.domain.action.transaction

import com.ivy.base.Constants
import com.ivy.frp.action.FPAction
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.tryOp
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class FetchAllTrnsFromServerAct @Inject constructor(
    restClient: RestClient,
    private val transactionDao: TransactionDao
) : FPAction<Unit, Res<Exception, Unit>>() {
    companion object {
        private const val MAX_LIMIT_IF_SHIT_HAPPENS = 1000
    }

    private val transactionService = restClient.transactionService

    override suspend fun Unit.compose(): suspend () -> Res<Exception, Unit> = tryOp(
        operation = ::fetch
    )

    private suspend fun fetch() {
        tailrec suspend fun fetchInternal(page: Int) {
            val transactions = transactionService.getPaginated(
                page = page,
                size = Constants.PAGE_TRANSACTIONS_SIZE
            ).transactions.map { it.toEntity() }

            transactionDao.save(transactions)

            if (transactions.isNotEmpty() && page < MAX_LIMIT_IF_SHIT_HAPPENS) {
                //recurse
                fetchInternal(page = page + 1)
            }
        }

        fetchInternal(page = 0)
    }
}