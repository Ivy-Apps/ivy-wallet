package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.common.time.time
import com.ivy.core.domain.pure.dummy.dummyActual
import com.ivy.core.ui.time.picker.time.TimePickerModal
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview


@Composable
fun BoxScope.TrnTimeModal(
    modal: IvyModal,
    trnTime: TrnTime,
    level: Int = 1,
    onTrnTimeChange: (TrnTime) -> Unit,
) {
    TimePickerModal(
        modal = modal,
        level = level,
        selected = trnTime.time().toLocalTime(),
        onPick = { time ->
            onTrnTimeChange(
                when (trnTime) {
                    is TrnTime.Actual -> TrnTime.Actual(
                        trnTime.actual
                            .withHour(time.hour)
                            .withMinute(time.minute)
                            .withSecond(0)
                            .withNano(0)
                    )
                    is TrnTime.Due -> TrnTime.Due(
                        trnTime.due
                            .withHour(time.hour)
                            .withMinute(time.minute)
                            .withSecond(0)
                            .withNano(0)
                    )
                }
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        TrnTimeModal(
            modal = modal,
            trnTime = dummyActual(),
            onTrnTimeChange = {}
        )
    }
}