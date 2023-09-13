package com.ivy.exchangerates.modal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.legacy.IvyWalletPreview
import com.ivy.design.l0_system.Orange
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.exchangerates.data.RateUi
import com.ivy.exchangerates.RatesEvent
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalAdd
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.AddRateModal(
    visible: Boolean,
    baseCurrency: String,
    dismiss: () -> Unit,
    onAdd: (RatesEvent.AddRate) -> Unit,
) {
    var toCurrency by remember { mutableStateOf("") }
    var amountModalVisible by remember { mutableStateOf(false) }
    var rate by remember { mutableStateOf<Double?>(null) }

    IvyModal(
        id = null,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalAdd {
                val to = toCurrency
                val finalRate = rate
                if (to.isNotBlank() && finalRate != null) {
                    onAdd(
                        RatesEvent.AddRate(
                            RateUi(
                                from = baseCurrency,
                                to = to,
                                rate = finalRate,
                            )
                        )
                    )
                    dismiss()
                }
            }
        }
    ) {
        SpacerVer(height = 16.dp)
        ModalTitle(text = "Add rate")
        SpacerVer(height = 24.dp)
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = toCurrency,
            label = {
                Text(text = "Currency")
            },
            onValueChange = { toCurrency = it },
        )
        SpacerVer(height = 12.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    amountModalVisible = true
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = "$baseCurrency-$toCurrency = ${rate ?: "???"}",
            style = UI.typo.nH2.style(
                color = Orange,
                textAlign = TextAlign.Center,
            )
        )
        SpacerVer(height = 24.dp)
    }

    AmountModal(
        id = remember { UUID.randomUUID() },
        visible = amountModalVisible,
        currency = "",
        initialAmount = rate,
        decimalCountMax = 12,
        dismiss = { amountModalVisible = false },
        onAmountChanged = {
            rate = it
        }
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        AddRateModal(
            visible = true,
            baseCurrency = "USD",
            dismiss = {},
            onAdd = {},
        )
    }
}
