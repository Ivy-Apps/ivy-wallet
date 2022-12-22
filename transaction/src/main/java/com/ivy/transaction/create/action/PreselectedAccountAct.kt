package com.ivy.transaction.create.action

import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.account.AccountsAct
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.transaction.create.action.PreselectedAccountAct.Input
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PreselectedAccountAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val accountByIdAct: AccountByIdAct,
    private val mapAccountUiAct: MapAccountUiAct,
    private val accountsAct: AccountsAct
) : Action<Input, AccountUi?>() {
    private val lastUsedAccountIdKey by lazy {
        stringPreferencesKey("last_used_account_id")
    }

    data class Input(
        val preselectedAccountId: String?,
    )

    override suspend fun Input.willDo(): AccountUi? =
        preselectedAccount() ?: lastUsedAccount() ?: firstAccount()

    private suspend fun Input.preselectedAccount(): AccountUi? =
        preselectedAccountId?.let { accountByIdAct(it) }
            ?.let { mapAccountUiAct(it) }

    private suspend fun lastUsedAccount(): AccountUi? =
        dataStore.get(lastUsedAccountIdKey).firstOrNull()
            ?.let { accountByIdAct(it) }
            ?.let { mapAccountUiAct(it) }

    private suspend fun firstAccount(): AccountUi? =
        accountsAct(Unit).firstOrNull()
            ?.let { mapAccountUiAct(it) }
}