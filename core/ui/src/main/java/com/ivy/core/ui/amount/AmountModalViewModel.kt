package com.ivy.core.ui.amount

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.data.Value
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class AmountModalViewModel @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowViewModel<AmountModalState, AmountModalState, AmountModalEvent>() {
    override fun initialState(): AmountModalState = AmountModalState(
        enteredText = null,
        currency = "",
        amount = null,
        amountBaseCurrency = null,
        calculatorResult = CalculatorResultUi(result = "", isError = true)
    )

    override fun initialUiState(): AmountModalState = initialState()

    private val enteredText = MutableStateFlow("")
    private val currency = MutableStateFlow("")

    override fun stateFlow(): Flow<AmountModalState> = combine(
        enteredText, currency, calculateFlow(), amountBaseCurrencyFlow()
    ) { enteredText, currency, calcResult, amountBaseCurrency ->
        AmountModalState(
            enteredText = enteredText,
            currency = currency,
            amount = calcResult.second?.let { Value(it, currency) },
            amountBaseCurrency = amountBaseCurrency,
            calculatorResult = calcResult.first
        )
    }

    private fun amountBaseCurrencyFlow(): Flow<ValueUi?> = combine(
        currency, baseCurrencyFlow(), calculateFlow(), exchangeRatesFlow()
    ) { currency, baseCurrency, calcResult, rates ->
        if (currency == baseCurrency) null else calcResult.second?.let {
            exchange(rates, from = currency, to = baseCurrency, amount = it).orNull()
        }?.let { exchangedAmount ->
            format(
                Value(amount = exchangedAmount, currency = baseCurrency),
                shortenFiat = true
            )
        }
    }

    private fun calculateFlow(): Flow<Pair<CalculatorResultUi, Double?>> =
        enteredText.map { input ->
            // 1,032.55 => 1032.55
            val expression = input.replace(localGroupingSeparator().toString(), "")
                .replace(localDecimalSeparator().toString(), ".")

            val evaluated = calculate(expression)
            CalculatorResultUi(
                result = evaluated?.toString() ?: "Error",
                isError = evaluated == null
            ) to evaluated
        }

    private fun calculate(expression: String): Double? = 0.0 // TODO:

    override suspend fun mapToUiState(state: AmountModalState): AmountModalState = state


    // region Event Handling
    override suspend fun handleEvent(event: AmountModalEvent) = when (event) {
        AmountModalEvent.Backspace -> handleBackspace()
        AmountModalEvent.DecimalSeparator -> handleDecimalSeparator()
        is AmountModalEvent.Calculator -> handleCalculator(event)
        is AmountModalEvent.Number -> handleNumber(event)
        is AmountModalEvent.CurrencyChange -> handleCurrencyChange(event)
        is AmountModalEvent.Initial -> handleInitial(event)
    }

    private fun handleBackspace() {
        if (enteredText.value.isNotEmpty()) {
            enteredText.value = enteredText.value.drop(1)
        }
    }

    private fun handleDecimalSeparator() {
        val decimalSeparator = localDecimalSeparator()
        if (!enteredText.value.contains(decimalSeparator)) {
            // an expression can have only one decimal separator
            enteredText.value += decimalSeparator
        }
    }

    // region Calculator options
    private fun handleCalculator(event: AmountModalEvent.Calculator): Unit = when (event.option) {
        CalculatorOption.Plus -> TODO()
        CalculatorOption.Minus -> TODO()
        CalculatorOption.Multiply -> TODO()
        CalculatorOption.Divide -> TODO()
        CalculatorOption.Brackets -> TODO()
        CalculatorOption.Percent -> TODO()
        CalculatorOption.Equals -> TODO()
        CalculatorOption.C -> TODO()
    }
    // endregion

    private fun handleNumber(event: AmountModalEvent.Number) {
        enteredText.value += event.number
    }

    private fun handleCurrencyChange(event: AmountModalEvent.CurrencyChange) {
        // TODO: Enter 10 BGN -> change to EUR -> amount should become 5
        val newCurrency = event.currency
        val currentCurrency = currency.value

        currency.value = newCurrency
        if (newCurrency != currentCurrency) {
            // TODO:
        }
    }

    private fun handleInitial(event: AmountModalEvent.Initial) {
        event.initialAmount?.let {
            currency.value = it.currency
            enteredText.value = format(it, shortenFiat = false).amount
        }
    }
    // endregion
}