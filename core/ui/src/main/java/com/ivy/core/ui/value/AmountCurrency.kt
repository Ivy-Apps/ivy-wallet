package com.ivy.core.ui.value

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.data.transaction.Value
import com.ivy.design.l0_system.Orange
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.ComponentPreviewBase

@Composable
fun Value.AmountCurrencyRow(
    color: Color = UI.colors.pureInverse,
    shortenBigNumbers: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AmountCurrency(color = color, shortenBigNumbers = shortenBigNumbers)
    }
}

@Composable
fun Value.AmountCurrency(
    color: Color = UI.colors.pureInverse,
    shortenBigNumbers: Boolean = false
) {
    val amountText = formatAmount(shortenBigNumbers = shortenBigNumbers)

    Text(
        modifier = Modifier.testTag("amount_currency_b1"),
        text = amountText,
        style = UI.typo.nB1.style(
            fontWeight = FontWeight.Bold,
            color = color
        )
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = currency,
        style = UI.typo.nB1.style(
            fontWeight = FontWeight.Normal,
            color = color
        )
    )
}

@Preview
@Composable
private fun Preview_Default() {
    ComponentPreviewBase {
        Value(
            amount = 150.0,
            currency = "BGN"
        ).AmountCurrencyRow()
    }
}

@Preview
@Composable
private fun Preview_Custom() {
    ComponentPreviewBase {
        Value(
            amount = 192_000.34,
            currency = "USD"
        ).AmountCurrencyRow(
            color = Orange,
            shortenBigNumbers = true
        )
    }
}