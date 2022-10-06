package com.ivy.core.ui.currency

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class CurrencyModalViewModel @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow
) : FlowViewModel<CurrencyModalState, CurrencyModalState, CurrencyModalEvent>() {
    override fun initialState(): CurrencyModalState = CurrencyModalState(
        items = emptyList()
    )

    override fun initialUiState(): CurrencyModalState = initialState()

    override fun stateFlow(): Flow<CurrencyModalState> {
        TODO("Not yet implemented")
    }

    override suspend fun mapToUiState(state: CurrencyModalState): CurrencyModalState = state

    // region Event Handling
    override suspend fun handleEvent(event: CurrencyModalEvent) {
        TODO("Not yet implemented")
    }
    // endregion
}