package com.ivy.accounts.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Body
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview

@Composable
internal fun BoxScope.NetWorthInfoModal(
    modal: IvyModal,
    totalBalance: ValueUi,
    availableBalance: ValueUi,
    excludedBalance: ValueUi,
) {
    Modal(
        modal = modal,
        actions = {
            Positive(text = "Got it") {
                modal.hide()
            }
        }
    ) {
        Title(text = "Net-worth")
        SpacerVer(height = 4.dp)
        Body(
            text = "Your net-worth is the combined value of all your assets" +
                    " minus your liabilities."
        )
        SpacerVer(height = 24.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            B2(text = "Available balance")
            Row {
                AmountCurrency(availableBalance)
            }
            B1Second(text = "+")
            B2(text = "Excluded balance")
            Row {
                AmountCurrency(excludedBalance, color = UI.colors.red)
            }
            B1Second(text = "=")
            B2(text = "Net-worth")
            Row {
                AmountCurrency(totalBalance, color = UI.colors.primary)
            }
        }
        SpacerVer(height = 24.dp)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        NetWorthInfoModal(
            modal = modal,
            totalBalance = dummyValueUi("203k"),
            availableBalance = dummyValueUi("136,3k"),
            excludedBalance = dummyValueUi("64,3k"),
        )
    }
}
// endregion