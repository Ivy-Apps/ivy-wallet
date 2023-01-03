package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.FeeModal(
    modal: IvyModal,
    fee: Value?,
    level: Int = 1,
    onRemoveFee: () -> Unit,
    onFeeChange: (Value) -> Unit,
) {
    AmountModal(
        modal = modal,
        level = level,
        key = "fee",
        initialAmount = fee,
        moreActions = {
            DeleteButton {
                onRemoveFee()
                modal.hide()
            }
            SpacerHor(width = 12.dp)
        },
        onAmountEnter = onFeeChange
    )
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        FeeModal(
            modal = modal,
            fee = null,
            onRemoveFee = {},
            onFeeChange = {}
        )
    }
}