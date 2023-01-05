package com.ivy.core.ui.amount.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.core.ui.amount.util.rememberDecimalSeparator
import com.ivy.data.CurrencyCode
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.H2Second
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.data.none
import com.ivy.design.l2_components.button.Btn
import com.ivy.design.l2_components.button.TextIcon
import com.ivy.design.util.ComponentPreview
import com.ivy.resources.R

@Composable
internal fun ColumnScope.AmountSection(
    calculatorVisible: Boolean,
    expression: String?,
    currency: CurrencyCode,
    amountInBaseCurrency: ValueUi?,
    calculatorTempResult: CalculatorResultUi?,
    onPickCurrency: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        H2Second(
            text = expression ?: "0${rememberDecimalSeparator()}00",
            fontWeight = FontWeight.Bold,
            color = if (expression != null)
                UI.colorsInverted.pure else UI.colors.neutral
        )
        AnimatedVisibility(
            visible = !calculatorVisible && currency.isNotBlank(),
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            CurrencyPicker(currency = currency, onClick = onPickCurrency)
        }
    }
    AnimatedAmountInBaseCurrency(
        calculatorVisible = calculatorVisible,
        amountInBaseCurrency = amountInBaseCurrency,
    )
    CalculatorTemporaryResult(
        calculatorVisible = calculatorVisible,
        result = calculatorTempResult,
    )
}

// region Currency Picker
@Composable
private fun CurrencyPicker(
    currency: CurrencyCode,
    onClick: () -> Unit
) {
    Btn.TextIcon(
        modifier = Modifier.padding(start = 8.dp),
        text = currency,
        iconRight = R.drawable.round_expand_more_24,
        iconPadding = 4.dp,
        background = none(),
        textStyle = UI.typoSecond.h2.style(
            color = UI.colors.primary,
            fontWeight = FontWeight.ExtraBold
        ),
        iconTint = UI.colors.primary,
        onClick = onClick
    )
}
// endregion

// region Amount in base currency
@Composable
private fun ColumnScope.AnimatedAmountInBaseCurrency(
    calculatorVisible: Boolean,
    amountInBaseCurrency: ValueUi?
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        visible = !calculatorVisible && amountInBaseCurrency != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .padding(top = 0.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            B1Second(
                text = amountInBaseCurrency?.amount ?: "",
                fontWeight = FontWeight.Normal
            )
            SpacerHor(width = 4.dp)
            B1Second(
                text = amountInBaseCurrency?.currency ?: "",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
// endregion

// region Calculator temp result
@Composable
private fun ColumnScope.CalculatorTemporaryResult(
    calculatorVisible: Boolean,
    result: CalculatorResultUi?,
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        visible = calculatorVisible && result != null
    ) {
        if (result != null) {
            B1Second(
                text = result.result,
                fontWeight = FontWeight.ExtraBold,
                color = if (result.isError) UI.colors.red else UI.colors.primary
            )
        }
    }
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            AmountSection(
                calculatorVisible = false,
                expression = null,
                currency = "USD",
                amountInBaseCurrency = ValueUi(
                    amount = "10.00",
                    currency = "BGN"
                ),
                calculatorTempResult = null,
                onPickCurrency = {}
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Calculator() {
    ComponentPreview {
        Column {
            AmountSection(
                calculatorVisible = true,
                expression = "5+5",
                currency = "USD",
                amountInBaseCurrency = null,
                calculatorTempResult = CalculatorResultUi(
                    result = "10.00",
                    isError = false
                ),
                onPickCurrency = {}
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Calculator_error() {
    ComponentPreview {
        Column {
            AmountSection(
                calculatorVisible = true,
                expression = "5+",
                currency = "USD",
                amountInBaseCurrency = null,
                calculatorTempResult = CalculatorResultUi(
                    result = "Error",
                    isError = true
                ),
                onPickCurrency = {}
            )
        }
    }
}
// endregion