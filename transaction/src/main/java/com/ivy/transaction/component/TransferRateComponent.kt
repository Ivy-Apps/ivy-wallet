package com.ivy.transaction.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.l0_system.UI
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.resources.R
import com.ivy.transaction.data.TransferRateUi

@Composable
fun TransferRateComponent(
    rate: TransferRateUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Custom(UI.colors.redP1),
        text = "${rate.fromCurrency}-${rate.toCurrency}: ${rate.rateValueFormatted}",
        icon = R.drawable.round_currency_exchange_24,
        typo = UI.typoSecond.b2,
        onClick = onClick,
    )
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        TransferRateComponent(
            rate = TransferRateUi(
                rateValueFormatted = "1.2",
                rateValue = 1.2,
                fromCurrency = "EUR",
                toCurrency = "USD",
            ),
            onClick = {}
        )
    }
}