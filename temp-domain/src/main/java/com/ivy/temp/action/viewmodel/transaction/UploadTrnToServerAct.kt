package com.ivy.wallet.domain.action.viewmodel.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.monad.Res
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class UploadTrnToServerAct @Inject constructor(
    private val ivySession: IvySession,
    restClient: RestClient,
    private val dao: TransactionDao
) : FPAction<Transaction, Res<String, Unit>>() {
    private val service = restClient.transactionService
    override suspend fun Transaction.compose(): suspend () -> Res<String, Unit> {
        TODO("Not yet implemented")
    }

//    override suspend fun Transaction.compose(): suspend () -> Res<Exception, Unit> =
//        ivySession::isLoggedIn then { loggedIn ->
//            if (loggedIn) Res.Ok(Unit) else Res.Err("User not logged in.")
//        } thenIfSuccess tryOp {
//            service.update(
//                UpdateTransactionRequest(
//                    transaction = this.toDTO()
//                )
//            )
//        } mapError {
//
//        } thenIfSuccess {
//            //flag as synced
//            dao.save(
//                this.copy(
//                    isSynced = true
//                ).toEntity()
//            )
//
//            Res.Ok(Unit)
//        }
//
//    suspend fun sync(item: Transaction) {
//        if (!ivySession.isLoggedIn()) return
//
//        try {
//            //update
//            service.update(
//                UpdateTransactionRequest(
//                    transaction = item.toDTO()
//                )
//            )
//
//            //flag as synced
//            dao.save(
//                item.copy(
//                    isSynced = true
//                ).toEntity()
//            )
//            Timber.d("Transaction updated: $item.")
//        } catch (e: Exception) {
//            Timber.e("Failed to update with error (${e.message}): $item")
//            e.printStackTrace()
//        }
//    }
}