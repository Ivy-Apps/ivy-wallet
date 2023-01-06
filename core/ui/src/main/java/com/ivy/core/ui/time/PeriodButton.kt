package com.ivy.core.ui.time

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.btnText
import com.ivy.core.ui.data.period.dummyMonthUi
import com.ivy.core.ui.data.period.dummyRangeUi
import com.ivy.core.ui.time.handling.SelectPeriodEvent
import com.ivy.core.ui.time.handling.SelectedPeriodViewModel
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.wallet.utils.horizontalSwipeListener

@Composable
fun PeriodButton(
    selectedPeriod: SelectedPeriodUi,
    periodModal: IvyModal,
    modifier: Modifier = Modifier,
) {
    val viewModel: SelectedPeriodViewModel? = hiltViewModelPreviewSafe()

    IvyButton(
        modifier = modifier.horizontalSwipeListener(
            sensitivity = 75,
            onSwipeLeft = {
                // next month
                viewModel?.onEvent(SelectPeriodEvent.ShiftForward)
            },
            onSwipeRight = {
                // previous month
                viewModel?.onEvent(SelectPeriodEvent.ShiftBackward)
            }
        ),
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = selectedPeriod.btnText(),
        icon = R.drawable.ic_round_calendar_month_24,
    ) {
        periodModal.show()
    }
}


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        PeriodButton(
            selectedPeriod = SelectedPeriodUi.Monthly(
                btnText = "September",
                month = dummyMonthUi(),
                rangeUi = dummyRangeUi()
            ),
            periodModal = rememberIvyModal()
        )
    }
}
// endregion