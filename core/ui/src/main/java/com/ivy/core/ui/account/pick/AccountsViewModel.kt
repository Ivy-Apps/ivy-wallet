package com.ivy.core.ui.account.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    accountsFlow: AccountsFlow,
    private val mapAccountUiAct: MapAccountUiAct,
) : SimpleFlowViewModel<AccountsState, Unit>() {
    override val initialUi = AccountsState(
        accounts = emptyList(),
    )

    override val uiFlow: Flow<AccountsState> = accountsFlow()
        .map { accounts ->
            AccountsState(
                accounts = accounts.map {
                    mapAccountUiAct(it)
                },
            )
        }


    override suspend fun handleEvent(event: Unit) {
    }
}