package com.ivy.core.ui.account.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.account.pick.data.SelectableAccountUi
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.data.account.AccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AccountPickerViewModel @Inject constructor(
    accountsFlow: AccountsFlow,
    private val mapAccountUiAct: MapAccountUiAct,
) : SimpleFlowViewModel<AccountPickerState, AccountPickerEvent>() {
    override val initialUi = AccountPickerState(
        accounts = emptyList()
    )

    private val selectedIds = MutableStateFlow(listOf<String>())

    override val uiFlow: Flow<AccountPickerState> = combine(
        accountsFlow(), selectedIds
    ) { accounts, selectedIds ->
        AccountPickerState(
            accounts = accounts.filter { it.state != AccountState.Archived }
                .map { mapAccountUiAct(it) }
                .map { SelectableAccountUi(it, selected = selectedIds.contains(it.id)) }
        )
    }

    // region Event Handling
    override suspend fun handleEvent(event: AccountPickerEvent) = when (event) {
        is AccountPickerEvent.SelectedChange -> handleSelectedChange(event)
    }

    private fun handleSelectedChange(event: AccountPickerEvent.SelectedChange) {
        selectedIds.value = event.selected.map { it.id }
    }
    // endregion
}