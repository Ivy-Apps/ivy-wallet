package com.ivy.exchangerates.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.exchangerates.data.RateUi
import com.ivy.legacy.IvyWalletComponentPreview
import com.ivy.legacy.utils.format
import com.ivy.wallet.ui.theme.components.DeleteButton

@Composable
fun RateItem(
    rate: RateUi,
    onDelete: (() -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 16.dp
            )
            .clickable(onClick = onClick)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            val currencyValue: Double = 1.0
            RateColumn(
                label = "Sell",
                rate = rate.from,
                value = currencyValue.format(currencyCode = rate.from)
            )

            SpacerHor(width = 16.dp)
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "arrow to next"
            )
            SpacerHor(width = 16.dp)
            RateColumn(
                label = "Buy",
                rate = rate.to,
                value = rate.rate.format(currencyCode = rate.to)
            )

            if (onDelete != null) {
                SpacerWeight(weight = 1f)
                DeleteButton(onClick = onDelete)
            }
        }
    }
}

@Composable
private fun RateColumn(label: String, rate: String, value: String) {
    Column {
        Text(
            text = label,
            style = UI.typo.c.style(
                fontWeight = FontWeight.Normal
            )
        )
        Text(
            text = rate,
            style = UI.typo.nB1.style(
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = value,
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Normal
            )
        )
    }
}

// region Preview
@Preview
@Composable
private fun Preview() {
    IvyWalletComponentPreview {
        RateItem(
            rate = RateUi(
            from = "BGN",
            to = "EUR",
            rate = 1.95583
        ),
            onDelete = null,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Delete() {
    IvyWalletComponentPreview {
        RateItem(
            rate = RateUi(
                from = "BGN",
                to = "EUR",
                rate = 1.95583
        ),
            onDelete = { },
            onClick = {}
        )
    }
}
// endregion
