package com.ivy.core.ui.time.picker.date

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
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

    Modal(
        modal = modal,
        level = level,
        actions = {

        }
    ) {
        Title(text = "Pick a date")
        SpacerVer(height = 24.dp)
        Row {
            SpacerWeight(weight = 1f)
            DayWheel(
                days = state.days,
                daysCount = state.daysListSize,
                onDayChange = {
                    viewModel?.onEvent(DatePickerEvent.DayChange(it))
                }
            )
            MonthWheel(
                months = state.months,
                monthsCount = state.monthsListSize,
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
    modifier: Modifier = Modifier,
    onDayChange: (PickerDay) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        items = days,
        itemsCount = daysCount,
        text = { it.text },
        onSelectedChange = onDayChange
    )
}

@Composable
private fun MonthWheel(
    months: List<PickerMonth>,
    monthsCount: Int,
    modifier: Modifier = Modifier,
    onMonthChange: (PickerMonth) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        items = months,
        itemsCount = monthsCount,
        text = { it.text },
        onSelectedChange = onMonthChange
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> WheelPicker(
    items: List<T>,
    itemsCount: Int,
    text: (T) -> String,
    modifier: Modifier = Modifier,
    onSelectedChange: (T) -> Unit,
) {
    val listState = rememberLazyListState()
    val selectedIndex by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex)
                .coerceIn(0 until itemsCount)
        }
    }

    LaunchedEffect(Unit) {
        // skip first spacer
        // skip first item
        // => select the 2nd (center item)
        listState.animateScrollToItem(index = 1)
    }

//    LaunchedEffect(selectedIndex) {
//        onSelectedChange(items[selectedIndex])
//    }

    val itemSize = 64.dp

    LazyColumn(
        modifier = modifier.height(3 * itemSize),
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        item(key = "space_zero") {
            SpacerVer(height = itemSize)
        }
        itemsIndexed(
            items = items,
            key = { index, _ -> index }
        ) { index, item ->
            B1Second(
                modifier = Modifier
                    .defaultMinSize(minWidth = 128.dp)
                    .height(itemSize),
                text = text(item),
                textAlign = TextAlign.Center,
                maxLines = 1,
                color = if (index == selectedIndex)
                    UI.colors.primary else UI.colorsInverted.pure
            )
        }
        item(key = "space_last") {
            SpacerVer(height = itemSize)
        }
    }
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
)
// endregion