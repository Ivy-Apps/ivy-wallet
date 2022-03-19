package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.base.format
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.theme.Orange


@Composable
fun CustomExchangeRateCard(
    modifier: Modifier = Modifier,
    title: String = "Exchange Rate",
    fromCurrencyCode: String,
    toCurrencyCode: String,
    exchangeRate: Double,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(icon = R.drawable.ic_currency)

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fromCurrencyCode,
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )
                IvyIcon(icon = R.drawable.ic_arrow_right, tint = Orange)
                Text(
                    text = "$toCurrencyCode \t\t:\t\t",
                    style = UI.typo.nB2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )
                Text(
                    text = exchangeRate.format(4),
                    style = UI.typo.nB2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_OneTime() {
    IvyWalletComponentPreview {
        CustomExchangeRateCard(
            fromCurrencyCode = "INR",
            toCurrencyCode = "EUR",
            exchangeRate = (86.2)
        ) {
        }
    }
}