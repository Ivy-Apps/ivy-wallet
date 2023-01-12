package com.ivy.transaction.create.action

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.transaction.create.persistence.LastUsedAccountIdKey
import javax.inject.Inject

class WriteLastUsedAccount @Inject constructor(
    private val dataStore: IvyDataStore,
    private val lastUsedAccountId: LastUsedAccountIdKey,
) : Action<WriteLastUsedAccount.Input, Unit>() {
    data class Input(
        val accountId: String,
    )

    override suspend fun action(input: Input) {
        dataStore.put(lastUsedAccountId.key, input.accountId)
    }
}