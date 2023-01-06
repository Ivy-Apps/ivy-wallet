package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.FeeModal(
    modal: IvyModal,
    fee: Value?,
    level: Int = 1,
    onRemoveFee: () -> Unit,
    onFeePercent: (Double) -> Unit,
    onFeeChange: (Value) -> Unit,
) {
    AmountModal(
        modal = modal,
        level = level,
        key = "fee",
        initialAmount = fee,
        contentAbove = {
            val feePercents = remember {
                listOf(
                    "0.25%" to 0.0025,
                    "0.5%" to 0.005,
                    "0.75%" to 0.0075,
                    "1%" to 0.01,
                    "1.25%" to 0.0125,
                    "1.5%" to 0.015,
                    "1.6%" to 0.016,
                    "1.75%" to 0.0175,
                    "1.8%" to 0.018,
                    "2%" to 0.02,
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                items(
                    items = feePercents,
                    key = { it.first }
                ) { (percentText, percentValue) ->
                    SpacerHor(8.dp)
                    IvyButton(
                        size = ButtonSize.Small,
                        visibility = Visibility.Medium,
                        feeling = Feeling.Positive,
                        typo = UI.typoSecond.b2,
                        fontWeight = FontWeight.Normal,
                        text = percentText,
                        icon = null,
                    ) {
                        onFeePercent(percentValue)
                    }
                }
                item(key = "last_item_spacer") {
                    SpacerHor(12.dp)
                }
            }
        },
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
            onFeeChange = {},
            onFeePercent = {}
        )
    }
}