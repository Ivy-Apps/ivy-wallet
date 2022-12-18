package com.ivy.core.ui.time.picker.date

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.time.picker.component.VerticalWheelPicker
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.DatePickerModal(
    modal: IvyModal,
    selected: LocalDate,
    level: Int = 1,
    onPick: (LocalDate) -> Unit,
) {
    val viewModel: DatePickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LaunchedEffect(selected) {
        viewModel?.onEvent(DatePickerEvent.Initial(selected))
    }

    Modal(
        modal = modal,
        level = level,
        actions = {
            Positive(text = "Choose") {
                onPick(state.selected)
                modal.hide()
            }
        }
    ) {
        Title(text = "Pick a date")
        SpacerVer(height = 24.dp)
        SpacerVer(height = 24.dp)
        Row {
            SpacerWeight(weight = 1f)
            DayWheel(
                days = state.days,
                daysCount = state.daysListSize,
                initialDayValue = selected.dayOfMonth - 1,
                onDayChange = {
                    viewModel?.onEvent(DatePickerEvent.DayChange(it))
                }
            )
            MonthWheel(
                months = state.months,
                monthsCount = state.monthsListSize,
                initialMonthValue = selected.monthValue - 1,
                onMonthChange = {
                    viewModel?.onEvent(DatePickerEvent.MonthChange(it))
                }
            )
            SpacerWeight(weight = 1f)
        }
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun DayWheel(
    days: List<PickerDay>,
    daysCount: Int,
    initialDayValue: Int,
    modifier: Modifier = Modifier,
    onDayChange: (PickerDay) -> Unit,
) {
    VerticalWheelPicker(
        modifier = modifier,
        items = days,
        itemsCount = daysCount,
        initialIndex = initialDayValue,
        text = { it.text },
        onSelectedChange = onDayChange
    )
}

@Composable
private fun MonthWheel(
    months: List<PickerMonth>,
    monthsCount: Int,
    initialMonthValue: Int,
    modifier: Modifier = Modifier,
    onMonthChange: (PickerMonth) -> Unit,
) {
    VerticalWheelPicker(
        modifier = modifier,
        items = months,
        itemsCount = monthsCount,
        initialIndex = initialMonthValue,
        text = { it.text },
        onSelectedChange = onMonthChange
    )
}



// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        DatePickerModal(
            modal = modal,
            selected = LocalDate.now(),
            onPick = {},
        )
    }
}

private fun previewState() = DatePickerState(
    days = emptyList(),
    daysListSize = 0,
    months = emptyList(),
    monthsListSize = 0,
    years = emptyList(),
    yearsListSize = 0,
    selected = LocalDate.now(),
)
// endregion