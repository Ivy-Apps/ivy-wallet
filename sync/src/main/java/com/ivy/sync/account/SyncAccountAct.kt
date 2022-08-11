package com.ivy.sync.account

import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.thenIfSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.request.account.DeleteAccountRequest
import com.ivy.wallet.io.network.request.account.UpdateAccountRequest
import com.ivy.wallet.io.network.service.AccountService
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class SyncAccountAct @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val ivySession: IvySession,
    private val accountService: AccountService
) : FPAction<IOEffect<Account>, Unit>() {
    override suspend fun IOEffect<Account>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<Account>) {
        if (!ivySession.isLoggedIn()) return

        when (operation) {
            is IOEffect.Delete -> delete(operation.item)
            is IOEffect.Save -> save(operation.item)
        }
    }

    private suspend fun delete(item: Account) = tryOp(
        operation = DeleteAccountRequest(id = item.id) asParamTo accountService::delete
    ) thenInvokeAfter {
        transactionDao.deleteAllByAccountId(accountId = item.id)
        accountDao.deleteById(item.id)
    }

    private suspend fun save(item: Account) = tryOp(
        operation = UpdateAccountRequest(
            account = mapToDTO(item)
        ) asParamTo accountService::update
    ) thenIfSuccess {
        val syncedItem = item.mark(
            isSynced = true, isDeleted = false
        )
        persist(syncedItem)
        Res.Ok(Unit)
    } thenInvokeAfter {}

    private suspend fun persist(item: Account) {
        accountDao.save(mapToEntity(item))
    }
}