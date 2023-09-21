package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.resources.R
import com.ivy.legacy.IvyWalletComponentPreview
import com.ivy.wallet.ui.theme.Orange
import com.ivy.legacy.utils.format

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun CustomExchangeRateCard(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.exchange_rate),
    fromCurrencyCode: String,
    toCurrencyCode: String,
    exchangeRate: Double,
    onRefresh: () -> Unit = {},
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(icon = R.drawable.ic_currency)

        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
        IvyIcon(
            icon = R.drawable.ic_refresh,
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable {
                    onRefresh()
                }
        )
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
