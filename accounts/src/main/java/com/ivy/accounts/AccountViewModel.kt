package com.ivy.accounts

import com.ivy.core.domain.FlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(

) : FlowViewModel<Unit, AccountState, AccountEvent>() {
    override val initialInternal = Unit
    override val initialUi: AccountState = AccountState(dummy = "")
    override val internalFlow = flow<Unit> {}
    override val uiFlow: Flow<AccountState> = flowOf(initialUi)

    override suspend fun handleEvent(event: AccountEvent) {
        TODO("Not yet implemented")
    }
}