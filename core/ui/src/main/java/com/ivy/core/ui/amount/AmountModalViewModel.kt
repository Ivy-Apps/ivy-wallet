package com.ivy.core.ui.amount

import com.ivy.common.isNotEmpty
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.data.Value
import com.ivy.math.calculator.appendDecimalSeparator
import com.ivy.math.calculator.appendTo
import com.ivy.math.evaluate
import com.ivy.math.formatNumber
import com.ivy.math.localDecimalSeparator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
internal class AmountModalViewModel @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowViewModel<AmountModalState, AmountModalState, AmountModalEvent>() {
    override fun initialState(): AmountModalState = AmountModalState(
        expression = null,
        currency = "",
        amount = null,
        amountBaseCurrency = null,
        calculatorResult = CalculatorResultUi(result = "", isError = true)
    )

    override fun initialUiState(): AmountModalState = initialState()

    private val expression = MutableStateFlow("")
    private val currency = MutableStateFlow("")

    override fun stateFlow(): Flow<AmountModalState> = combine(
        expression, currency, calculateFlow(), amountBaseCurrencyFlow()
    ) { expression, currency, calcResult, amountBaseCurrency ->
        val nonEmptyExpression = expression.takeIf { it.isNotEmpty() }
        AmountModalState(
            expression = nonEmptyExpression,
            currency = currency,
            amount = calcResult.second?.let { Value(it, currency) },
            amountBaseCurrency = amountBaseCurrency,
            calculatorResult = calcResult.first.takeIf { nonEmptyExpression != null }
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
        expression.map { expression ->
            val evaluated = evaluate(expression)
            CalculatorResultUi(
                result = evaluated?.let(::formatNumber) ?: "Error",
                isError = evaluated == null
            ) to evaluated
        }

    override suspend fun mapToUiState(state: AmountModalState): AmountModalState = state


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
        }
    }

    private fun handleDecimalSeparator() {
        expression.value = appendDecimalSeparator(
            expression = expression.value,
            decimalSeparator = localDecimalSeparator(),
        )
    }

    // region Calculator
    private fun handleCalculatorOperator(event: AmountModalEvent.CalculatorOperator) {
        expression.value = appendTo(expression = expression.value, operator = event.operator)
    }

    private fun handleCalculatorC() {
        expression.value = ""
    }

    private fun handleCalculatorEquals() {
        val evaluated = evaluate(expression.value)
        if (evaluated != null) {
            expression.value = format(Value(evaluated, currency.value), shortenFiat = false).amount
        }
    }
    // endregion

    private fun handleNumber(event: AmountModalEvent.Number) {
        expression.value += event.number
    }

    private suspend fun handleCurrencyChange(event: AmountModalEvent.CurrencyChange) {
        val currency = currency.value
        val newCurrency = event.currency

        val enteredValue = state.value.amount
        Timber.d("enteredValue = $enteredValue")
        if (newCurrency != currency && enteredValue != null) {
            // Converted the entered amount to the new currency
            // TODO: Fix this ugly workaround
            val latestRates = exchangeRatesFlow().take(1).toList().last()
            Timber.d("latestRates = $latestRates")
            exchange(
                ratesData = latestRates,
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
    }

    private fun handleInitial(event: AmountModalEvent.Initial) {
        event.initialAmount?.let { value ->
            currency.value = value.currency
            if (value.amount != 0.0) {
                expression.value = format(value, shortenFiat = false).amount
            }
        }
    }
    // endregion
}