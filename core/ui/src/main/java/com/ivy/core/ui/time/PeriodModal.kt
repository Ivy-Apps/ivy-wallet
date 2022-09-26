package com.ivy.core.ui.time

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.dateNowLocal
import com.ivy.core.domain.pure.time.allTime
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.*
import com.ivy.core.ui.time.handling.PeriodModalEvent
import com.ivy.core.ui.time.handling.SelectedPeriodHandlerViewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerH
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.B1
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Set
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.viewModelPreviewSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@Composable
fun BoxScope.PeriodModal(
    modal: IvyModal,
    selectedPeriod: SelectedPeriodUi
) {
    val viewModel: SelectedPeriodHandlerViewModel? = viewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()
    UI(
        modal = modal,
        selectedPeriod = selectedPeriod,
        state = state,
        onEvent = { viewModel?.onEvent(it) },
    )
}

@Composable
private fun BoxScope.UI(
    modal: IvyModal,
    selectedPeriod: SelectedPeriodUi,
    state: SelectedPeriodHandlerViewModel.State,
    onEvent: (PeriodModalEvent) -> Unit,
) {
    Modal(
        modal = modal,
        actions = {
            Set {
                modal.hide()
            }
        }
    ) {
        ChooseMonth(months = state.months, selected = selectedPeriod, onEvent = onEvent)

        SpacerVer(height = 16.dp)
        DividerH(width = 1.dp)
        SpacerVer(height = 12.dp)

        FromToRange(selected = selectedPeriod, onEvent = onEvent)

        InTheLast(selected = selectedPeriod)
        AllTime(selected = selectedPeriod)
    }
}

// region Choose month
@Composable
private fun ChooseMonth(
    months: List<MonthUi>,
    selected: SelectedPeriodUi,
    onEvent: (PeriodModalEvent) -> Unit,
) {
    SpacerVer(height = 16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        "Month".B1(
            fontWeight = FontWeight.ExtraBold,
            color = if (selected is SelectedPeriodUi.Monthly)
                UI.colors.primary else UI.colorsInverted.pure
        )
        SpacerWeight(weight = 1f)
        IvyButton(
            modifier = Modifier.alignByBaseline(),
            size = ButtonSize.Small,
            visibility = ButtonVisibility.Low,
            feeling = ButtonFeeling.Positive,
            text = "RESET",
            icon = null,
        ) {
            onEvent(PeriodModalEvent.ResetToCurrentPeriod)
        }
    }
    SpacerVer(height = 8.dp)

    val state = rememberLazyListState()
    val selectedMonth = (selected as? SelectedPeriodUi.Monthly)?.month
    LaunchedEffect(selected) {
        if (selectedMonth != null) {
            val selectedMonthIndex = withContext(Dispatchers.Default) {
                months.indexOf(selectedMonth).takeIf { it != -1 }
            }
            if (selectedMonthIndex != null) {
                state.scrollToItem(selectedMonthIndex)
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
                onEvent(PeriodModalEvent.Monthly(month))
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
        visibility = if (selected) ButtonVisibility.High else ButtonVisibility.Medium,
        feeling = ButtonFeeling.Positive,
        text = month.fullName,
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
    onEvent: (PeriodModalEvent) -> Unit,
) {
    val periodUi = selected.periodUi()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodClosureColumn(
            label = "From",
            dateText = periodUi.fromText,
            onDateSelected = {
                // TODO
            }
        )
        SpacerHor(width = 16.dp)
        PeriodClosureColumn(
            label = "To",
            dateText = periodUi.toText,
            onDateSelected = {
                // TODO
            }
        )
    }
}

@Composable
private fun RowScope.PeriodClosureColumn(
    label: String,
    dateText: String,
    onDateSelected: (LocalDateTime) -> Unit
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        label.B1(
            modifier = Modifier.padding(start = 16.dp),
            fontWeight = FontWeight.Bold
        )
        SpacerVer(height = 4.dp)
        DateButton(
            dateText = dateText,
            onDateSelected = onDateSelected,
        )
    }
}

@Composable
private fun DateButton(
    modifier: Modifier = Modifier,
    dateText: String,
    onDateSelected: (LocalDateTime) -> Unit
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Big,
        visibility = ButtonVisibility.Medium,
        feeling = ButtonFeeling.Positive,
        text = dateText,
        icon = R.drawable.ic_round_calendar_month_24
    ) {
        // TODO: Pick a date
    }

}

// endregion

@Composable
private fun InTheLast(
    selected: SelectedPeriodUi
) {

}

@Composable
private fun AllTime(
    selected: SelectedPeriodUi
) {

}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        PeriodModal(
            modal = modal,
            selectedPeriod = SelectedPeriodUi.Monthly(
                month = MonthUi(
                    number = 2,
                    year = dateNowLocal().year,
                    fullName = fullMonthName(LocalContext.current, monthNumber = 2),
                ),
                periodUi = PeriodUi(
                    period = allTime(),
                    fromText = "Feb. 01",
                    toText = "Feb. 28"
                )
            )
        )
    }
}

@Composable
private fun previewState() = SelectedPeriodHandlerViewModel.State(
    startDayOfMonth = 1,
    months = monthsList(LocalContext.current, year = dateNowLocal().year)
)
// endregion