package com.ivy.core.ui.amount

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.resources.R

/**
 * @param key used to refresh the initial amount when the key changes
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.AmountModal(
    modal: IvyModal,
    initialAmount: Value?,
    level: Int = 1,
    calculatorVisible: MutableState<Boolean> = remember { mutableStateOf(false) },
    key: String? = null,
    contentAbove: (@Composable ModalScope.() -> Unit)? = {
        SpacerVer(height = 24.dp)
    },
    moreActions: (@Composable ModalActionsScope.() -> Unit)? = null,
    onAmountEnter: (Value) -> Unit,
) {
    val viewModel: AmountModalViewModel? = hiltViewModelPreviewSafe(key = key)
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(initialAmount, key) {
        viewModel?.onEvent(AmountModalEvent.Initial(initialAmount))
    }

    val currencyPickerModal = rememberIvyModal()

    Modal(
        modal = modal,
        level = level,
        contentModifier = Modifier.verticalScroll(rememberScrollState()),
        actions = {
            moreActions?.invoke(this)
            Secondary(
                text = null,
                icon = R.drawable.ic_vue_edu_calculator,
                feeling = if (calculatorVisible.value)
                    Feeling.Negative else Feeling.Positive,
                hapticFeedback = true
            ) {
                calculatorVisible.value = !calculatorVisible.value
                if (!calculatorVisible.value) {
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
        // Close the software keyboard if it's open
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            keyboardController?.hide()
        }

        contentAbove?.invoke(this)
        AmountSection(
            calculatorVisible = calculatorVisible.value,
            expression = state.expression,
            currency = state.currency,
            amountInBaseCurrency = state.amountBaseCurrency,
            calculatorTempResult = state.calculatorResult,
            onPickCurrency = { currencyPickerModal.show() }
        )
        SpacerVer(height = 12.dp)
        Keyboard(
            calculatorVisible = calculatorVisible.value,
            onCalculatorEvent = { viewModel?.onEvent(AmountModalEvent.CalculatorOperator(it)) },
            onNumberEvent = { viewModel?.onEvent(AmountModalEvent.Number(it)) },
            onDecimalSeparator = { viewModel?.onEvent(AmountModalEvent.DecimalSeparator) },
            onBackspace = { viewModel?.onEvent(AmountModalEvent.Backspace) },
            onCalculatorC = { viewModel?.onEvent(AmountModalEvent.CalculatorC) },
            onCalculatorEquals = { viewModel?.onEvent(AmountModalEvent.CalculatorEquals) }
        )
        SpacerVer(height = 16.dp)
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