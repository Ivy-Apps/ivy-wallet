package com.ivy.core.ui.value

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.B2Second
import com.ivy.design.l1_buildingBlocks.H2Second
import com.ivy.design.l1_buildingBlocks.SpacerHor

@Suppress("unused")
@Composable
fun ColumnScope.AmountCurrencyBig(
    value: ValueUi,
    color: Color = UI.colorsInverted.pure,
) {
    H2Second(
        text = value.amount,
        modifier = Modifier.testTag("amount_currency_b1"),
        fontWeight = FontWeight.Bold,
        color = color,
    )
    B1Second(
        text = value.currency,
        fontWeight = FontWeight.Normal,
        color = color,
    )
}

@Suppress("unused")
@Composable
fun RowScope.AmountCurrencyBig(
    value: ValueUi,
    color: Color = UI.colorsInverted.pure,
) {
    H2Second(
        text = value.amount,
        modifier = Modifier.testTag("amount_currency_b1"),
        fontWeight = FontWeight.Bold,
        color = color,
    )
    SpacerHor(width = 4.dp)
    B1Second(
        text = value.currency,
        fontWeight = FontWeight.SemiBold,
        color = color,
    )
}

@Suppress("unused")
@Composable
fun RowScope.AmountCurrency(
    value: ValueUi,
    color: Color = UI.colorsInverted.pure,
) {
    B1Second(
        text = value.amount,
        modifier = Modifier.testTag("amount_currency_b1"),
        fontWeight = FontWeight.Bold,
        color = color,
    )
    SpacerHor(width = 4.dp)
    B1Second(
        text = value.currency,
        fontWeight = FontWeight.Normal,
        color = color,
    )
}

@Suppress("unused")
@Composable
fun RowScope.AmountCurrencySmall(
    value: ValueUi,
    color: Color = UI.colorsInverted.pure,
) {
    B2Second(
        text = value.amount,
        modifier = Modifier.testTag("amount_currency_b1"),
        fontWeight = FontWeight.Bold,
        color = color,
    )
    SpacerHor(width = 4.dp)
    B2Second(
        text = value.currency,
        fontWeight = FontWeight.Normal,
        color = color,
    )
}