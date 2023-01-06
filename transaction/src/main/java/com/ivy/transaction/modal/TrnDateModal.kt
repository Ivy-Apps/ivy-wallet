package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.time.time
import com.ivy.core.domain.pure.dummy.dummyActual
import com.ivy.core.ui.time.picker.date.DatePickerModal
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l0_system.color.Orange
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

private enum class TrnTimeTypeLocal {
    Actual, Due
}

@Composable
fun BoxScope.TrnDateModal(
    modal: IvyModal,
    trnTime: TrnTime,
    level: Int = 1,
    onTrnTimeChange: (TrnTime) -> Unit,
) {
    var type by remember {
        mutableStateOf(
            when (trnTime) {
                is TrnTime.Actual -> TrnTimeTypeLocal.Actual
                is TrnTime.Due -> TrnTimeTypeLocal.Due
            }
        )
    }

    DatePickerModal(
        modal = modal,
        level = level,
        selected = trnTime.time().toLocalDate(),
        contentTop = {
            SpacerVer(height = 16.dp)
            TrnTimeTypeSelector(
                type = type,
                onTypeChange = { type = it }
            )
        },
        onPick = { date ->
            val time = trnTime.time().toLocalTime()
            onTrnTimeChange(
                when (type) {
                    TrnTimeTypeLocal.Actual -> TrnTime.Actual(date.atTime(time))
                    TrnTimeTypeLocal.Due -> TrnTime.Due(date.atTime(time))
                }
            )
        }
    )
}

@Composable
private fun TrnTimeTypeSelector(
    type: TrnTimeTypeLocal,
    onTypeChange: (TrnTimeTypeLocal) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Small,
            visibility = when (type) {
                TrnTimeTypeLocal.Actual -> Visibility.High
                TrnTimeTypeLocal.Due -> Visibility.Medium
            },
            feeling = Feeling.Positive,
            text = "Actual",
            icon = null
        ) {
            onTypeChange(TrnTimeTypeLocal.Actual)
        }
        SpacerHor(width = 16.dp)
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Small,
            visibility = when (type) {
                TrnTimeTypeLocal.Actual -> Visibility.Medium
                TrnTimeTypeLocal.Due -> Visibility.High
            },
            feeling = Feeling.Custom(Orange),
            text = "Due",
            icon = null
        ) {
            onTypeChange(TrnTimeTypeLocal.Due)
        }
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        TrnDateModal(
            modal = modal,
            trnTime = dummyActual(),
            onTrnTimeChange = {}
        )
    }
}