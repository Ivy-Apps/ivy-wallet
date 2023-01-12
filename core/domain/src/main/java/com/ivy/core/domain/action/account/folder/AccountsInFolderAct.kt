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
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(folderId: String): List<Account> {
        val folderUUID = folderId.toUUID()
        return accountsFlow().take(1).first().filter { it.folderId == folderUUID }
    }
}