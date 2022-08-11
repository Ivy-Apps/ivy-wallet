package com.ivy.sync.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.thenIfSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.request.transaction.DeleteTransactionRequest
import com.ivy.wallet.io.network.request.transaction.UpdateTransactionRequest
import com.ivy.wallet.io.network.service.TransactionService
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject


class SyncTrnAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val ivySession: IvySession,
    private val transactionService: TransactionService,
) : FPAction<IOEffect<Transaction>, Unit>() {

    override suspend fun IOEffect<Transaction>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<Transaction>) {
        if (!ivySession.isLoggedIn()) return

        when (operation) {
            is IOEffect.Delete -> delete(operation.item)
            is IOEffect.Save -> save(operation.item)
        }
    }

    private suspend fun delete(item: Transaction) = tryOp(
        operation = DeleteTransactionRequest(id = item.id) asParamTo transactionService::delete
    ) thenInvokeAfter {
        transactionDao.deleteById(item.id)
    }

    private suspend fun save(item: Transaction) = tryOp(
        operation = UpdateTransactionRequest(
            transaction = mapToDTO(item)
        ) asParamTo transactionService::update
    ) thenIfSuccess {
        val syncedTrn = item.mark(
            isSynced = true,
            isDeleted = false
        )
        persist(syncedTrn)
        Res.Ok(Unit)
    } thenInvokeAfter {}

    private suspend fun persist(transaction: Transaction) {
        transactionDao.save(mapToEntity(transaction))
    }
}