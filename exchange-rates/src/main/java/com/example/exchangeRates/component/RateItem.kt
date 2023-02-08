package com.example.exchangeRates.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exchangeRates.data.RateUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.util.IvyPreview
import kotlin.math.round


//Individual Rate item composable for the main ExchangeRate screen
@Composable
fun RateItem(
    rate: RateUi,
    onDelete: (() -> Unit)?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, UI.colors.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${rate.from}-${rate.to}:",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Normal
            )
        )
        SpacerHor(width = 8.dp)
        Text(
            //rounding the rate value to 5 decimal points
            text = "%.5f".format(round(rate.rate*100000)/100000),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.SemiBold
            )
        )
        if (onDelete != null) {
            SpacerWeight(weight = 1f)
            DeleteButton(onClick = onDelete)
        }
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
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
    IvyPreview {
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