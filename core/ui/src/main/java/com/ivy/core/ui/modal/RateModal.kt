package com.ivy.core.ui.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.H2Second
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.RateModal(
    modal: IvyModal,
    rate: Double,
    fromCurrency: String,
    toCurrency: String,
    level: Int = 1,
    key: String = "default",
    onRateChange: (Double) -> Unit,
) {
    AmountModal(
        modal = modal,
        level = level,
        key = key,
        contentAbove = {
            SpacerVer(height = 24.dp)
            H2Second(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "$fromCurrency to $toCurrency",
                color = UI.colors.primary,
                textAlign = TextAlign.Center,
            )
            SpacerVer(height = 24.dp)
        },
        initialAmount = Value(
            amount = rate,
            currency = "",
        ),
        onAmountEnter = {
            onRateChange(it.amount)
        },
    )
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        RateModal(
            modal = modal,
            rate = 1.95,
            fromCurrency = "EUR",
            toCurrency = "BGN",
            onRateChange = {},
        )
    }
}