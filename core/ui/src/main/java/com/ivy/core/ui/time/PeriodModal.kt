package com.ivy.core.ui.time

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.common.time.atEndOfDay
import com.ivy.common.time.dateNowLocal
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.*
import com.ivy.core.ui.modal.ViewModelModal
import com.ivy.core.ui.rootScreen
import com.ivy.core.ui.time.handling.SelectPeriodEvent
import com.ivy.core.ui.time.handling.SelectedPeriodViewModel
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.components.Done
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: Re-work and make optimal

@Composable
fun BoxScope.PeriodModal(
    modal: IvyModal,
) {
    val moreOptionsModal = rememberIvyModal()

    ViewModelModal(
        modal = modal,
        provideViewModel = { hiltViewModel<SelectedPeriodViewModel>() },
        previewState = { previewState() },
        actions = { _, _ ->
            Done {
                modal.hide()
            }
        }
    ) { state, onEvent ->
        ChooseMonth(
            months = state.months,
            selected = state.selectedPeriodUi,
        ) {
            onEvent(SelectPeriodEvent.Monthly(it))
        }

        SpacerVer(height = 16.dp)
        DividerHor(width = 1.dp, color = UI.colors.neutral)
        SpacerVer(height = 12.dp)

        FromToRange(selected = state.selectedPeriodUi, onEvent = onEvent)

        SpacerVer(height = 16.dp)
        DividerHor(width = 1.dp, color = UI.colors.neutral)
        SpacerVer(height = 12.dp)

        MoreOptions(
            selected = state.selectedPeriodUi,
            onEvent = onEvent,
            onShowMoreOptionsModal = {
                moreOptionsModal.show()
            }
        )

        SpacerVer(height = 48.dp)
    }

    MoreOptionsModal(
        periodModal = modal,
        moreOptionsModal = moreOptionsModal,
    )
}


// region Choose month
@Composable
private fun ChooseMonth(
    months: List<MonthUi>,
    selected: SelectedPeriodUi,
    onMonthSelected: (MonthUi) -> Unit,
) {
    SpacerVer(height = 16.dp)
    val selectedMonthly = selected is SelectedPeriodUi.Monthly
    SectionText(
        text = "Month",
        selected = selectedMonthly,
        modifier = Modifier.padding(start = 24.dp),
    )
    SpacerVer(height = 8.dp)

    val state = rememberLazyListState()
    val selectedMonth = (selected as? SelectedPeriodUi.Monthly)?.month
    var firstTimeScrolling by remember { mutableStateOf(true) }
    LaunchedEffect(selected) {
        if (selectedMonth != null) {
            val selectedMonthIndex = withContext(Dispatchers.Default) {
                months.indexOf(selectedMonth).takeIf { it != -1 }
            }
            if (selectedMonthIndex != null) {
                if (firstTimeScrolling) {
                    state.scrollToItem(selectedMonthIndex)
                    firstTimeScrolling = false
                } else {
                    state.animateScrollToItem(selectedMonthIndex)
                }
            }
        }
    }

    LazyRow(
        state = state
    ) {
        item {
            SpacerHor(width = 8.dp)
        }
        items(months) { month ->
            MonthItem(month = month, selected = month == selectedMonth) {
                onMonthSelected(it)
            }
            SpacerHor(width = 8.dp)
        }
    }
}

@Composable
private fun MonthItem(
    month: MonthUi,
    selected: Boolean,
    onClick: (MonthUi) -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = if (selected) Visibility.High else Visibility.Medium,
        feeling = if (selected) Feeling.Positive else Feeling.Neutral,
        text = if (month.currentYear) month.fullName else "${month.fullName}, ${month.year}",
        icon = null
    ) {
        onClick(month)
    }
}
// endregion

// region From - To
@Composable
private fun FromToRange(
    selected: SelectedPeriodUi,
    onEvent: (SelectPeriodEvent) -> Unit,
) {
    val periodUi = selected.rangeUi
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedCustom = selected is SelectedPeriodUi.CustomRange ||
                selected is SelectedPeriodUi.InTheLast
        val rootScreen = rootScreen()
        PeriodClosureColumn(
            label = "From",
            dateText = periodUi.fromText,
            selectedCustom = selectedCustom,
        ) {
            rootScreen.datePicker(
                minDate = null,
                maxDate = periodUi.range.to.toLocalDate(),
                initialDate = periodUi.range.from.toLocalDate()
            ) { pickedDate ->
                onEvent(
                    SelectPeriodEvent.CustomRange(
                        range = TimeRange(
                            from = pickedDate.atStartOfDay(),
                            to = periodUi.range.to
                        )
                    )
                )
            }
        }
        SpacerHor(width = 16.dp)
        PeriodClosureColumn(
            label = "To",
            dateText = periodUi.toText,
            selectedCustom = selectedCustom,
        ) {
            rootScreen.datePicker(
                minDate = periodUi.range.from.toLocalDate(),
                maxDate = null,
                initialDate = periodUi.range.to.toLocalDate()
            ) { pickedDate ->
                onEvent(
                    SelectPeriodEvent.CustomRange(
                        range = TimeRange(
                            from = periodUi.range.from,
                            to = pickedDate.atEndOfDay()
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun RowScope.PeriodClosureColumn(
    selectedCustom: Boolean,
    label: String,
    dateText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        SectionText(
            text = label,
            selected = selectedCustom,
            modifier = Modifier.padding(start = 16.dp),
        )
        SpacerVer(height = 4.dp)
        DateButton(
            dateText = dateText,
            onClick = onClick,
        )
    }
}

@Composable
private fun DateButton(
    modifier: Modifier = Modifier,
    dateText: String,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Neutral,
        text = dateText,
        icon = R.drawable.ic_round_calendar_month_24,
        onClick = onClick
    )

}
// endregion

// region More Options
@Composable
private fun MoreOptions(
    selected: SelectedPeriodUi,
    onEvent: (SelectPeriodEvent) -> Unit,
    onShowMoreOptionsModal: () -> Unit,
) {
    val selectedMore = selected is SelectedPeriodUi.AllTime
    SectionText(
        text = "More options",
        selected = selectedMore,
        modifier = Modifier.padding(start = 24.dp),
    )
    SpacerVer(height = 8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = if (selected is SelectedPeriodUi.AllTime)
                Visibility.High else Visibility.Medium,
            feeling = Feeling.Positive,
            text = "All-time",
            icon = R.drawable.ic_baseline_all_inclusive_24
        ) {
            onEvent(SelectPeriodEvent.AllTime)
        }
        SpacerHor(width = 8.dp)
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Negative,
            text = "Reset",
            icon = R.drawable.ic_round_undo_24
        ) {
            onEvent(SelectPeriodEvent.ResetToCurrentPeriod)
        }
    }
    SpacerVer(height = 8.dp)
    IvyButton(
        modifier = Modifier.padding(horizontal = 8.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Low,
        feeling = Feeling.Neutral,
        text = "See more",
        icon = R.drawable.ic_round_expand_less_24,
        onClick = onShowMoreOptionsModal
    )
}
// endregion

// region More Options modal
@Composable
private fun BoxScope.MoreOptionsModal(
    periodModal: IvyModal,
    moreOptionsModal: IvyModal,
) {
    ViewModelModal(
        modal = moreOptionsModal,
        provideViewModel = { hiltViewModel<SelectedPeriodViewModel>() },
        previewState = { previewState() },
        actions = { _, _ -> }
    ) { _, onEvent ->
        Title(text = "More Options")
        SpacerVer(height = 24.dp)
        val thisYear = remember { dateNowLocal().year.toString() }
        val lastYear = remember { (dateNowLocal().year - 1).toString() }
        LazyColumn {
            val onOptionItemClick = { event: SelectPeriodEvent ->
                onEvent(event)
                moreOptionsModal.hide()
                periodModal.hide()
            }
            optionItem(
                text = thisYear,
                event = SelectPeriodEvent.ThisYear,
                onClick = onOptionItemClick
            )
            optionItem(
                text = lastYear,
                event = SelectPeriodEvent.LastYear,
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 6 months",
                event = SelectPeriodEvent.InTheLast(n = 6, unit = TimeUnit.Month),
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 3 months",
                event = SelectPeriodEvent.InTheLast(n = 3, unit = TimeUnit.Month),
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 30 days",
                event = SelectPeriodEvent.InTheLast(n = 30, unit = TimeUnit.Day),
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 15 days",
                event = SelectPeriodEvent.InTheLast(n = 15, unit = TimeUnit.Day),
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 7 days",
                event = SelectPeriodEvent.InTheLast(n = 7, unit = TimeUnit.Day),
                onClick = onOptionItemClick
            )
            optionItem(
                text = "Last 3 days",
                event = SelectPeriodEvent.InTheLast(n = 3, unit = TimeUnit.Day),
                onClick = onOptionItemClick
            )
        }
        SpacerVer(height = 48.dp)
    }
}

private fun LazyListScope.optionItem(
    text: String,
    event: SelectPeriodEvent,
    onClick: (SelectPeriodEvent) -> Unit
) {
    item {
        MoreOptionsButton(text = text) {
            onClick(event)
        }
        SpacerVer(height = 8.dp)
    }
}

@Composable
private fun MoreOptionsButton(
    text: String,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = text,
        icon = null,
        onClick = onClick
    )
}
// endregion

// region Components
@Composable
private fun SectionText(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    B1(
        text = text,
        modifier = modifier,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
        color = if (selected) UI.colors.primary else UI.colorsInverted.pure
    )
}
// endregion

// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        PeriodModal(
            modal = modal,
        )
    }
}

@Composable
private fun previewState() = SelectedPeriodViewModel.UiState(
    startDayOfMonth = 1,
    months = monthsList(LocalContext.current, year = dateNowLocal().year, currentYear = true),
    selectedPeriodUi = SelectedPeriodUi.Monthly(
        periodBtnText = "Sep",
        month = dummyMonthUi(),
        rangeUi = dummyRangeUi()
    )
)
// endregion