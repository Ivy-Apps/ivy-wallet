package com.ivy.exchangeRates.modal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Orange
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.Text
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.DynamicSave
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview


@Composable
fun BoxWithConstraintsScope.AddRateModal(
    modal: IvyModal,
    baseCurrency: String,
    dismiss: () -> Unit,
    onAdd: (toCurrency: String, exchangeRate: Double) -> Unit,
) {
    var toCurrency by remember { mutableStateOf("") }
    val amountModal = rememberIvyModal()
    var rate by remember { mutableStateOf<Double?>(null) }

    Modal(
        modal = modal,
        actions = {
            DynamicSave(item = null) {
                val to = toCurrency
                val finalRate = rate
                if (to.isNotBlank() && finalRate != null) {
                    onAdd(to, finalRate)
                    dismiss()
                }
            }
        }
    ) {
        SpacerVer(height = 16.dp)
        Title(text = "Add rate")
        SpacerVer(height = 24.dp)
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = toCurrency,
            label = {
                Text(text = "Currency", typo = UI.typo.b2)
            },
            onValueChange = { toCurrency = it },
        )
        SpacerVer(height = 12.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    amountModal.show()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = "${baseCurrency}-${toCurrency} = ${rate ?: "???"}",
            typo = UI.typo.h2,
            color = Orange,
            textAlign = TextAlign.Center,
        )
        SpacerVer(height = 24.dp)
    }


    AmountModal(
        modal = amountModal,
        initialAmount = Value(rate ?: 0.0, ""),
        onAmountEnter = { newRate ->
            rate = newRate.amount
        }
    )
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        AddRateModal(
            modal = modal,
            baseCurrency = "USD",
            dismiss = {},
            onAdd = { to, rate -> },
        )
    }
}