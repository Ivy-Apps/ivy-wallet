package com.ivy.core.domain.action.account.folder

import com.ivy.common.toUUID
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.data.account.Account
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class AccountsInFolderAct @Inject constructor(
    private val accountsFlow: AccountsFlow,
) : Action<String, List<Account>>() {
    override suspend fun String.willDo(): List<Account> {
        val folderId = this.toUUID()
        return accountsFlow().take(1).first().filter { it.folderId == folderId }
    }
}