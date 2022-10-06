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
import com.ivy.core.ui.amount.rememberDecimalSeparator
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
    enteredText: String?,
    currency: CurrencyCode,
    amountInBaseCurrency: ValueUi?,
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
            text = enteredText ?: "0${rememberDecimalSeparator()}00",
            fontWeight = FontWeight.Bold,
            color = if (enteredText != null)
                UI.colorsInverted.pure else UI.colors.neutral
        )
        AnimatedVisibility(
            visible = !calculatorVisible,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            CurrencyPicker(currency = currency, onClick = onPickCurrency)
        }
    }
    AmountInBaseCurrency(
        calculatorVisible = calculatorVisible,
        amountInBaseCurrency = amountInBaseCurrency,
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
private fun ColumnScope.AmountInBaseCurrency(
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


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            AmountSection(
                calculatorVisible = false,
                enteredText = null,
                currency = "USD",
                amountInBaseCurrency = ValueUi(
                    amount = "10.00",
                    currency = "BGN"
                ),
                onPickCurrency = {}
            )
        }
    }
}
// endregion