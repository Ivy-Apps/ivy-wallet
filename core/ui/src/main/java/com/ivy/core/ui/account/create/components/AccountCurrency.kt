package com.ivy.core.ui.account.create.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.data.CurrencyCode
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Composable
internal fun ColumnScope.AccountCurrency(
    currency: CurrencyCode,
    color: Color,
    modifier: Modifier = Modifier,
    onPickCurrency: () -> Unit
) {
    B1(
        modifier = Modifier.padding(start = 24.dp),
        text = "Account currency"
    )
    SpacerVer(height = 8.dp)
    IvyButton(
        modifier = modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Custom(color),
        text = currency,
        icon = R.drawable.round_currency_exchange_24,
        onClick = onPickCurrency
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            AccountCurrency(
                currency = "BGN",
                color = Purple,
                onPickCurrency = {}
            )
        }
    }
}
// endregion