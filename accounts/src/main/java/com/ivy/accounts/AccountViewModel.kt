package com.ivy.accounts

import com.ivy.core.domain.FlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(

) : FlowViewModel<AccountState, AccountState, AccountEvent>() {
    override fun initialState() = AccountState(dummy = "dummy")

    override fun initialUiState(): AccountState = initialState()

    override fun stateFlow(): Flow<AccountState> = flow {

    }

    override suspend fun mapToUiState(state: AccountState): AccountState = state

    override suspend fun handleEvent(event: AccountEvent) {
        TODO("Not yet implemented")
    }

}