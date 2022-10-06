package com.ivy.core.ui.amount

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.toColor
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.design.util.thenWhen
import com.ivy.resources.R

private const val keypadOuterWeight = 1f
private const val keypadInnerWeight = 0.25f
private val keypadButtonBig = 64.dp
private val keypadButtonSmall = 56.dp
private val keyboardVerticalMargin = 12.dp

@Composable
fun BoxScope.AmountModal(
    modal: IvyModal,
    initialAmount: Value,
    contentAbove: (@Composable () -> Unit)? = {
        SpacerVer(height = 24.dp)
    },
    onAmountEnter: (Value) -> Unit,
) {
    val viewModel: AmountModalViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(initialAmount) {
        viewModel?.onEvent(AmountModalEvent.Initial(initialAmount))
    }

    var calculatorVisible by remember { mutableStateOf(false) }

    Modal(
        modal = modal,
        actions = {
            Secondary(
                text = null,
                icon = R.drawable.ic_vue_edu_calculator,
                feeling = if (calculatorVisible) ButtonFeeling.Negative else ButtonFeeling.Positive
            ) {
                calculatorVisible = !calculatorVisible
            }
            SpacerHor(width = 8.dp)
            Positive(
                text = stringResource(R.string.enter),
                icon = R.drawable.ic_round_check_24
            ) {
            }
        }
    ) {
        contentAbove?.invoke()
        Amount(
            amountText = state.amountText,
            currency = state.currency
        )
        Keyboard(
            calculatorVisible = calculatorVisible,
            onAmountChange = {},
            onCurrencyChange = {},
        )
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun Amount(
    amountText: String,
    currency: CurrencyCode,
) {

}

// region Keyboard
@Composable
private fun Keyboard(
    calculatorVisible: Boolean,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (CurrencyCode) -> Unit,
) {
    val onSymbolClick = { symbol: String ->
        // TODO:
    }
    val keypadBtnSize by animateDpAsState(
        targetValue = if (calculatorVisible)
            keypadButtonSmall else keypadButtonBig
    )
    CalculatorTopRow(
        calculatorVisible = calculatorVisible,
        keypadBtnSize = keypadBtnSize,
        onSymbolClick = onSymbolClick
    )
    // margin is built-in in calculator's top row
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "7", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "8", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "9", size = keypadBtnSize, onClick = onSymbolClick)
        CalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "*",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "4", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "5", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "6", size = keypadBtnSize, onClick = onSymbolClick)
        CalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "-",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "1", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "2", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "3", size = keypadBtnSize, onClick = onSymbolClick)
        CalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "+",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(
            symbol = rememberDecimalSeparator().toString(),
            size = keypadBtnSize,
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "0", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        BackSpaceButton(size = keypadBtnSize) {
            // TODO: Handle backspace
        }
        CalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "=",
            feeling = ButtonFeeling.Positive,
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
}

@Composable
private fun CalculatorTopRow(
    calculatorVisible: Boolean,
    keypadBtnSize: Dp,
    onSymbolClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = calculatorVisible,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        KeyboardRow(
            modifier = Modifier.padding(bottom = keyboardVerticalMargin)
        ) {
            SpacerWeight(weight = keypadOuterWeight)
            KeypadButton(
                symbol = "C",
                size = keypadBtnSize,
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Negative,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "( )",
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                size = keypadBtnSize,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "%",
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                size = keypadBtnSize,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "/",
                size = keypadBtnSize,
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadOuterWeight)
        }
    }
}

@Composable
private fun KeyboardRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.CalculatorButton(
    calculatorVisible: Boolean,
    symbol: String,
    modifier: Modifier = Modifier,
    feeling: ButtonFeeling = ButtonFeeling.Positive,
    onClick: (String) -> Unit
) {
    if (calculatorVisible) {
        SpacerWeight(weight = keypadInnerWeight)
    }
    AnimatedVisibility(
        visible = calculatorVisible,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        KeypadButton(
            modifier = modifier,
            feeling = feeling,
            visibility = ButtonVisibility.High,
            size = keypadButtonSmall,
            symbol = symbol,
            onClick = onClick
        )
    }
}

@Composable
private fun KeypadButton(
    symbol: String,
    size: Dp,
    modifier: Modifier = Modifier,
    visibility: ButtonVisibility = ButtonVisibility.Medium,
    feeling: ButtonFeeling = ButtonFeeling.Positive,
    onClick: (String) -> Unit
) {
    KeypadButtonBox(
        modifier = modifier,
        feeling = feeling,
        visibility = visibility,
        size = size,
        onClick = { onClick(symbol) }
    ) {
        B1Second(
            text = symbol,
            color = when (visibility) {
                ButtonVisibility.Focused,
                ButtonVisibility.High ->
                    rememberContrastColor(feeling.toColor())
                ButtonVisibility.Medium,
                ButtonVisibility.Low -> UI.colorsInverted.pure
            },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BackSpaceButton(
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    KeypadButtonBox(
        modifier = modifier,
        feeling = ButtonFeeling.Negative,
        size = size,
        onClick = onClick,
    ) {
        IconRes(
            icon = R.drawable.outline_backspace_24,
            tint = UI.colorsInverted.pure,
        )
    }
}

@Composable
private fun KeypadButtonBox(
    feeling: ButtonFeeling,
    size: Dp,
    modifier: Modifier = Modifier,
    visibility: ButtonVisibility = ButtonVisibility.Medium,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(UI.shapes.circle)
            .thenWhen {
                when (visibility) {
                    ButtonVisibility.Focused,
                    ButtonVisibility.High -> background(
                        color = feeling.toColor(),
                        shape = UI.shapes.circle
                    )
                    ButtonVisibility.Low,
                    ButtonVisibility.Medium -> border(
                        width = 1.dp,
                        color = feeling.toColor(),
                        shape = UI.shapes.circle
                    )
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}
// endregion


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
    amountText = "",
    currency = "USD",
    amount = null,
    amountBaseCurrency = null
)
// endregion