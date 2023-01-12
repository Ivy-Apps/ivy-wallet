package com.ivy.transaction.create.action

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.account.AccountsAct
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.transaction.create.action.PreselectedAccountAct.Input
import com.ivy.transaction.create.persistence.LastUsedAccountIdKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PreselectedAccountAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val accountByIdAct: AccountByIdAct,
    private val mapAccountUiAct: MapAccountUiAct,
    private val accountsAct: AccountsAct,
    private val lastUsedAccountId: LastUsedAccountIdKey,
) : Action<Input, AccountUi?>() {

    data class Input(
        val preselectedAccountId: String?,
    )

    override suspend fun action(input: Input): AccountUi? =
        input.preselectedAccount() ?: lastUsedAccount() ?: firstAccount()

    private suspend fun Input.preselectedAccount(): AccountUi? =
        preselectedAccountId?.let { accountByIdAct(it) }
            ?.let { mapAccountUiAct(it) }

    private suspend fun lastUsedAccount(): AccountUi? =
        dataStore.get(lastUsedAccountId.key).firstOrNull()
            ?.let { accountByIdAct(it) }
            ?.let { mapAccountUiAct(it) }

    private suspend fun firstAccount(): AccountUi? =
        accountsAct(Unit).firstOrNull()
            ?.let { mapAccountUiAct(it) }
}