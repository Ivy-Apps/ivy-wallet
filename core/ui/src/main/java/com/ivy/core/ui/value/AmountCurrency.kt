package com.ivy.core.ui.value

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.B1Second

@Composable
fun FormattedValue.AmountCurrencyRow(
    color: Color = UI.colorsInverted.pure,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AmountCurrency(color = color)
    }
}

@Composable
fun FormattedValue.AmountCurrency(
    color: Color = UI.colorsInverted.pure,
) {
    amount.B1Second(
        modifier = Modifier.testTag("amount_currency_b1"),
        fontWeight = FontWeight.Bold,
        color = color,
    )
    SpacerHor(width = 4.dp)
    currency.B1Second(
        fontWeight = FontWeight.Normal,
        color = color,
    )
}