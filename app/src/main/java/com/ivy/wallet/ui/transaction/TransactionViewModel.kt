package com.ivy.wallet.ui.transaction

import com.ivy.frp.viewmodel.FRPViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(

) : FRPViewModel<TrnState, TrnEvent>() {
    override val _state: MutableStateFlow<TrnState> = MutableStateFlow(TrnState.Initial)

    override suspend fun handleEvent(event: TrnEvent): suspend () -> TrnState {
        TODO("Not yet implemented")
    }

}