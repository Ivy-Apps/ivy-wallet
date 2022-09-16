package com.ivy.sync.ivyserver.account

import com.ivy.data.account.Account
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.mapSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import com.ivy.sync.base.SyncItem
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.data.AccountDTO
import com.ivy.wallet.io.network.request.account.DeleteAccountRequest
import com.ivy.wallet.io.network.request.account.UpdateAccountRequest
import com.ivy.wallet.io.network.service.AccountService
import javax.inject.Inject

class AccountIvyServerSync @Inject constructor(
    private val ivySession: IvySession,
    private val accountService: AccountService
) : SyncItem<Account> {
    override suspend fun enabled(): SyncItem<Account>? = this.takeIf { ivySession.isLoggedIn() }

    override suspend fun save(items: List<Account>): List<Account> = items.map { saveItem(it) }
        .mapNotNull { if (it is Res.Ok) it.data else null }

    private suspend fun saveItem(item: Account): Res<Exception, Account> = tryOp(
        operation = UpdateAccountRequest(
            account = mapToDTO(item)
        ) asParamTo accountService::update
    ) mapSuccess { item } thenInvokeAfter { it }

    override suspend fun delete(items: List<Account>): List<Account> =
        items.map { deleteItem(it) }
            .mapNotNull { if (it is Res.Ok) it.data else null }

    private suspend fun deleteItem(item: Account): Res<Exception, Account> = tryOp(
        operation = DeleteAccountRequest(
            id = item.id
        ) asParamTo accountService::delete
    ) mapSuccess { item } thenInvokeAfter { it }

    override suspend fun get(): Res<Unit, List<Account>> {
        TODO("Not yet implemented")
    }

    private fun mapToDTO(acc: Account): AccountDTO = TODO()
}

