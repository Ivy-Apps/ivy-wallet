package com.ivy.core.ui.account.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.data.account.AccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AccountPickerViewModel @Inject constructor(
    accountsFlow: AccountsFlow,
    private val mapAccountUiAct: MapAccountUiAct,
) : SimpleFlowViewModel<AccountPickerState, Unit>() {
    override val initialUi = AccountPickerState(
        accounts = emptyList()
    )

    override val uiFlow: Flow<AccountPickerState> = accountsFlow().map { accounts ->
        AccountPickerState(
            accounts = accounts.filter { it.state != AccountState.Archived }
                .map { mapAccountUiAct(it) }
        )
    }

    override suspend fun handleEvent(event: Unit) {}
}