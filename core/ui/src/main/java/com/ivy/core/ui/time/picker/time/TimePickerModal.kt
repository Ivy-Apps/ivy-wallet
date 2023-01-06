package com.ivy.core.ui.time.picker.time

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.time.picker.component.HorizontalWheelPicker
import com.ivy.core.ui.time.picker.component.VerticalWheelPicker
import com.ivy.core.ui.time.picker.time.data.PickerHour
import com.ivy.core.ui.time.picker.time.data.PickerMinute
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.H2Second
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import java.time.LocalTime

@Composable
fun BoxScope.TimePickerModal(
    modal: IvyModal,
    selected: LocalTime,
    level: Int = 1,
    contentTop: @Composable ModalScope.() -> Unit = {},
    onPick: (LocalTime) -> Unit,
) {
    val viewModel: TimePickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LaunchedEffect(selected) {
        viewModel?.onEvent(TimePickerEvent.Initial(selected))
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
        Title(text = "Pick a time")
        contentTop()
        SpacerVer(height = 24.dp)
        if (state.amPm != null) {
            AmPmWheel(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                initialAmPmValue = state.amPm,
                onAmPmChange = {
                    viewModel?.onEvent(TimePickerEvent.AmPmChange(it))
                }
            )
            SpacerVer(height = 16.dp)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpacerWeight(weight = 1f)
            HoursWheel(
                hours = state.hours,
                hoursCount = state.hoursListSize,
                initialHourIndex = state.selectedHourIndex,
                onHourChange = { viewModel?.onEvent(TimePickerEvent.HourChange(it)) }
            )
            SpacerHor(width = 12.dp)
            H2Second(
                text = ":",
                fontWeight = FontWeight.Bold,
                color = UI.colors.primary,
            )
            SpacerHor(width = 12.dp)
            MinuteWheel(
                minutes = state.minutes,
                minutesCount = state.minutesListSize,
                initialMinute = selected.minute,
                onMinuteChange = { viewModel?.onEvent(TimePickerEvent.MinuteChange(it)) }
            )
            SpacerWeight(weight = 1f)
        }
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun HoursWheel(
    hours: List<PickerHour>,
    hoursCount: Int,
    initialHourIndex: Int,
    modifier: Modifier = Modifier,
    onHourChange: (PickerHour) -> Unit,
) {
    VerticalWheelPicker(
        modifier = modifier,
        items = hours,
        itemsCount = hoursCount,
        initialIndex = initialHourIndex,
        text = { it.text },
        onSelectedChange = onHourChange
    )
}

@Composable
private fun MinuteWheel(
    minutes: List<PickerMinute>,
    minutesCount: Int,
    initialMinute: Int,
    modifier: Modifier = Modifier,
    onMinuteChange: (PickerMinute) -> Unit,
) {
    VerticalWheelPicker(
        modifier = modifier,
        items = minutes,
        itemsCount = minutesCount,
        initialIndex = initialMinute,
        text = { it.text },
        onSelectedChange = onMinuteChange
    )
}

@Composable
private fun AmPmWheel(
    initialAmPmValue: AmPm,
    modifier: Modifier = Modifier,
    onAmPmChange: (AmPm) -> Unit,
) {
    HorizontalWheelPicker(
        modifier = modifier,
        items = listOf(
            AmPm.AM to "AM",
            AmPm.PM to "PM",
            AmPm.AM to "AM",
            AmPm.PM to "PM",
        ),
        itemsCount = 4,
        initialIndex = when (initialAmPmValue) {
            AmPm.AM -> 0
            AmPm.PM -> 1
        },
        text = { it.second },
        onSelectedChange = { (amPm, _) ->
            onAmPmChange(amPm)
        }
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        TimePickerModal(
            modal = modal,
            selected = LocalTime.now(),
            onPick = {},
        )
    }
}

private fun previewState() = TimePickerState(
    amPm = AmPm.PM,
    hours = (1..11).map { PickerHour(it.toString().padStart(2, '0'), it) },
    hoursListSize = 1,
    minutes = (0..59).map { PickerMinute(it.toString().padStart(2, '0'), it) },
    minutesListSize = 60,
    selected = LocalTime.now(),
    selectedHourIndex = LocalTime.now().hour,
)
// endregion