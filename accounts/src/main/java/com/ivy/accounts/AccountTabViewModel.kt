package com.ivy.accounts

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.design.l2_components.modal.IvyModal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AccountTabViewModel @Inject constructor(
    accountsFlow: AccountsFlow,
    private val mapAccountUiAct: MapAccountUiAct
) : SimpleFlowViewModel<AccountTabState, AccountTabEvent>() {
    override val initialUi: AccountTabState = AccountTabState(
        items = emptyList(),
        createAccountModal = IvyModal()
    )

    override val uiFlow: Flow<AccountTabState> = accountsFlow().map { accounts ->
        AccountTabState(
            items = emptyList(), // TODO:
            createAccountModal = initialUi.createAccountModal
        )
    }

    // region Event Handling
    override suspend fun handleEvent(event: AccountTabEvent) = when (event) {
        is AccountTabEvent.BottomBarAction -> handleBottomBarAction(event)
    }

    private fun handleBottomBarAction(event: AccountTabEvent.BottomBarAction) {
        // TODO: Handle properly
        uiState.value.createAccountModal.show()
    }
    // endregion
}