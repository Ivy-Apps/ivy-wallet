package com.ivy.core.ui.amount

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class AmountModalViewModel @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow
) : FlowViewModel<AmountModalState, AmountModalState, AmountModalEvent>() {
    override fun initialState(): AmountModalState = AmountModalState(
        enteredText = null,
        currency = "",
        amount = null,
        amountBaseCurrency = null
    )

    override fun initialUiState(): AmountModalState = initialState()

    private val amountText = MutableStateFlow("")
    private val currency = MutableStateFlow("")

    override fun stateFlow(): Flow<AmountModalState> =
        combine(amountText, currency) { amount, currency ->
            initialState()
            // TODO:
        }

    override suspend fun mapToUiState(state: AmountModalState): AmountModalState = state

    // region Event Handling
    override suspend fun handleEvent(event: AmountModalEvent) = when (event) {
        is AmountModalEvent.AmountChange -> handleAmountChange(event)
        is AmountModalEvent.CurrencyChange -> handleCurrencyChange(event)
        is AmountModalEvent.Initial -> handleInitial(event)
    }

    private fun handleAmountChange(event: AmountModalEvent.AmountChange) {
        amountText.value = event.amount
    }

    private fun handleCurrencyChange(event: AmountModalEvent.CurrencyChange) {
        currency.value = event.currency
    }

    private fun handleInitial(event: AmountModalEvent.Initial) {
        // TODO:
    }
    // endregion
}