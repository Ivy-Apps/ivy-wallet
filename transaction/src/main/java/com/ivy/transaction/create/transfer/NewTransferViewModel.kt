package com.ivy.transaction.create.transfer

import com.ivy.core.domain.SimpleFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewTransferViewModel @Inject constructor(

) : SimpleFlowViewModel<NewTransferState, NewTransferEvent>() {
    override val initialUi: NewTransferState
        get() = TODO("Not yet implemented")

    override val uiFlow: Flow<NewTransferState>
        get() = TODO("Not yet implemented")

    override suspend fun handleEvent(event: NewTransferEvent) {
        TODO("Not yet implemented")
    }
}