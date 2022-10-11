package com.ivy.core.ui.amount

import com.ivy.common.isNotBlank
import com.ivy.common.isNotEmpty
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.data.Value
import com.ivy.data.exchange.ExchangeRatesData
import com.ivy.math.calculator.appendDecimalSeparator
import com.ivy.math.calculator.appendTo
import com.ivy.math.calculator.beautify
import com.ivy.math.calculator.hasObviousResult
import com.ivy.math.evaluate
import com.ivy.math.formatNumber
import com.ivy.math.localDecimalSeparator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class AmountModalViewModel @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowViewModel<AmountModalViewModel.State, AmountModalState, AmountModalEvent>() {
    override fun initialState(): State = State(
        exchangeRates = ExchangeRatesData(baseCurrency = "", rates = emptyMap()),
        uiState = initialUiState()
    )

    override fun initialUiState(): AmountModalState = AmountModalState(
        expression = null,
        currency = "",
        amount = null,
        amountBaseCurrency = null,
        calculatorResult = CalculatorResultUi(result = "", isError = true)
    )

    private val expression = MutableStateFlow("")
    private val currency = MutableStateFlow("")
    private val showExpressionError = MutableStateFlow(false)

    private var overrideExpressionForInitial = false

    override fun stateFlow(): Flow<State> = combine(
        expression, currency, calculateFlow(), amountBaseCurrencyFlow(), exchangeRatesFlow()
    ) { expression, currency, (calcResult, expressionValue), amountBaseCurrency, exchangeRates ->
        State(
            exchangeRates = exchangeRates,
            uiState = AmountModalState(
                expression = beautify(expression),
                currency = currency,
                amount = expressionValue?.let { Value(it, currency) },
                amountBaseCurrency = amountBaseCurrency,
                calculatorResult = calcResult.takeIf {
                    calcResult.isError || !hasObviousResult(expression, expressionValue)

                }
            )
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

    override suspend fun mapToUiState(state: State): AmountModalState = state.uiState

    private fun calculateFlow(): Flow<Pair<CalculatorResultUi, Double?>> = combine(
        expression, showExpressionError
    ) { expression, showExpressionError ->
        val evaluated = evaluate(expression)
        CalculatorResultUi(
            result = evaluated?.let(::formatNumber) ?: "Error",
            isError = evaluated == null && showExpressionError
        ) to evaluated
    }


    // region Event Handling
    override suspend fun handleEvent(event: AmountModalEvent) = when (event) {
        AmountModalEvent.Backspace -> handleBackspace()
        AmountModalEvent.DecimalSeparator -> handleDecimalSeparator()
        is AmountModalEvent.CalculatorOperator -> handleCalculatorOperator(event)
        is AmountModalEvent.Number -> handleNumber(event)
        is AmountModalEvent.CurrencyChange -> handleCurrencyChange(event)
        is AmountModalEvent.Initial -> handleInitial(event)
        AmountModalEvent.CalculatorC -> handleCalculatorC()
        AmountModalEvent.CalculatorEquals -> handleCalculatorEquals()
    }

    private fun handleBackspace() {
        if (expression.value.isNotEmpty()) {
            expression.value = expression.value.dropLast(1)
            overrideExpressionForInitial = false // expression is not initial, disable override
        }
    }

    private fun handleDecimalSeparator() {
        expression.value = appendDecimalSeparator(
            expression = expression.value,
            decimalSeparator = localDecimalSeparator(),
        )
        overrideExpressionForInitial = false // expression is not initial, disable override
    }

    // region Calculator
    private fun handleCalculatorOperator(event: AmountModalEvent.CalculatorOperator) {
        expression.value = appendTo(expression = expression.value, operator = event.operator)
        overrideExpressionForInitial = false // expression is not initial, disable override
    }

    private fun handleCalculatorC() {
        expression.value = ""
        overrideExpressionForInitial = false // expression is not initial, disable override
    }

    private fun handleCalculatorEquals() {
        val evaluated = evaluate(expression.value)
        if (evaluated != null) {
            expression.value = format(Value(evaluated, currency.value), shortenFiat = false).amount
        } else if (expression.value.isNotBlank()) {
            showExpressionError.value = true
        }
        overrideExpressionForInitial = false // expression is not initial, disable override
    }
    // endregion

    private fun handleNumber(event: AmountModalEvent.Number) {
        // for better UX, allow the user to override the initial expression
        val currentExpression = if (overrideExpressionForInitial) "" else expression.value
        expression.value = appendTo(currentExpression, digit = event.number)
        showExpressionError.value = false // remove shown error

        overrideExpressionForInitial = false // expression is not initial, disable override
    }

    private suspend fun handleCurrencyChange(event: AmountModalEvent.CurrencyChange) {
        val currency = currency.value
        val newCurrency = event.currency

        val state = state.value
        val enteredValue = state.uiState.amount
        Timber.d("enteredValue = $enteredValue")
        if (newCurrency != currency && enteredValue != null) {
            // Converted the entered amount to the new currency
            exchange(
                ratesData = state.exchangeRates,
                from = currency, to = newCurrency,
                amount = enteredValue.amount
            ).orNull()?.let { exchangedAmount ->
                expression.value = format(
                    Value(exchangedAmount, newCurrency), shortenFiat = false
                ).amount
            }
        }

        // update the currency in the UI
        this.currency.value = newCurrency

        overrideExpressionForInitial = false // expression is not initial, disable override
    }

    private fun handleInitial(event: AmountModalEvent.Initial) {
        event.initialAmount?.let { value ->
            currency.value = value.currency
            if (value.amount != 0.0) {
                expression.value = format(value, shortenFiat = false).amount
                overrideExpressionForInitial = true
            }
        }
    }
    // endregion

    data class State(
        val exchangeRates: ExchangeRatesData,
        val uiState: AmountModalState
    )
}