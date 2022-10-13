package com.ivy.core.ui.amount

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.amount.components.AmountSection
import com.ivy.core.ui.amount.components.Keyboard
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.data.Value
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.resources.R

@Composable
fun BoxScope.AmountModal(
    modal: IvyModal,
    initialAmount: Value?,
    contentAbove: (@Composable () -> Unit)? = {
        SpacerVer(height = 32.dp)
    },
    onAmountEnter: (Value) -> Unit,
) {
    val viewModel: AmountModalViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(initialAmount) {
        viewModel?.onEvent(AmountModalEvent.Initial(initialAmount))
    }

    var calculatorVisible by remember { mutableStateOf(false) }
    val currencyPickerModal = rememberIvyModal()

    Modal(
        modal = modal,
        actions = {
            Secondary(
                text = null,
                icon = R.drawable.ic_vue_edu_calculator,
                feeling = if (calculatorVisible)
                    Feeling.Negative else Feeling.Positive,
                hapticFeedback = true
            ) {
                calculatorVisible = !calculatorVisible
                if (!calculatorVisible) {
                    viewModel?.onEvent(AmountModalEvent.CalculatorEquals)
                }
            }
            SpacerHor(width = 8.dp)
            Positive(
                text = stringResource(R.string.enter),
                icon = R.drawable.ic_round_check_24
            ) {
                state.amount?.let(onAmountEnter)
                modal.hide()
            }
        }
    ) {
        contentAbove?.invoke()
        AmountSection(
            calculatorVisible = calculatorVisible,
            expression = state.expression,
            currency = state.currency,
            amountInBaseCurrency = state.amountBaseCurrency,
            calculatorTempResult = state.calculatorResult,
            onPickCurrency = { currencyPickerModal.show() }
        )
        SpacerVer(height = 32.dp)
        Keyboard(
            calculatorVisible = calculatorVisible,
            onCalculatorEvent = { viewModel?.onEvent(AmountModalEvent.CalculatorOperator(it)) },
            onNumberEvent = { viewModel?.onEvent(AmountModalEvent.Number(it)) },
            onDecimalSeparator = { viewModel?.onEvent(AmountModalEvent.DecimalSeparator) },
            onBackspace = { viewModel?.onEvent(AmountModalEvent.Backspace) },
            onCalculatorC = { viewModel?.onEvent(AmountModalEvent.CalculatorC) },
            onCalculatorEquals = { viewModel?.onEvent(AmountModalEvent.CalculatorEquals) }
        )
        SpacerVer(height = 24.dp)
    }

    CurrencyPickerModal(
        modal = currencyPickerModal,
        level = 2,
        initialCurrency = state.currency,
        onCurrencyPick = { viewModel?.onEvent(AmountModalEvent.CurrencyChange(it)) }
    )
}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        AmountModal(
            modal = modal,
            initialAmount = Value(0.0, "USD"),
            onAmountEnter = {}
        )
    }
}

private fun previewState() = AmountModalState(
    expression = "500.00",
    currency = "USD",
    amount = null,
    amountBaseCurrency = ValueUi("1,032.55", "BGN"),
    calculatorResult = CalculatorResultUi(result = "", isError = true)
)
// endregion