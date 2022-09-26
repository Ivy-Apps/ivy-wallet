package com.ivy.core.ui.time

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.dateNowLocal
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.MonthUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.monthsList
import com.ivy.core.ui.time.handling.PeriodModalEvent
import com.ivy.core.ui.time.handling.SelectedPeriodHandlerViewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Set
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.viewModelPreviewSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    var internalPeriod by remember(selectedPeriod) {
        mutableStateOf(selectedPeriod)
    }

    Modal(
        modal = modal,
        actions = {
            Set {
                onEvent(PeriodModalEvent.SetPeriod(internalPeriod))
            }
        }
    ) {
        val setSelected = { newPeriod: SelectedPeriodUi ->
            internalPeriod = newPeriod
        }

        ChooseMonth(months = state.months, selected = internalPeriod, setSelected = setSelected)
        FromToRange(selected = internalPeriod, setSelected = setSelected)
        InTheLast(selected = internalPeriod, setSelected = setSelected)
        AllTime(selected = internalPeriod, setSelected = setSelected)
    }
}

// region Choose month
@Composable
private fun ModalScope.ChooseMonth(
    months: List<MonthUi>,
    selected: SelectedPeriodUi,
    setSelected: (SelectedPeriodUi) -> Unit
) {
    Title(
        text = stringResource(R.string.choose_month),
        color = if (selected is SelectedPeriodUi.Monthly)
            UI.colors.primary else UI.colorsInverted.pure
    )
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
                setSelected(SelectedPeriodUi.Monthly(month = it))
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

@Composable
private fun FromToRange(
    selected: SelectedPeriodUi,
    setSelected: (SelectedPeriodUi) -> Unit
) {

}

@Composable
private fun InTheLast(
    selected: SelectedPeriodUi,
    setSelected: (SelectedPeriodUi) -> Unit
) {

}

@Composable
private fun AllTime(
    selected: SelectedPeriodUi,
    setSelected: (SelectedPeriodUi) -> Unit
) {

}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        PeriodModal(modal = modal, selectedPeriod = SelectedPeriodUi.AllTime)
    }
}

@Composable
private fun previewState() = SelectedPeriodHandlerViewModel.State(
    startDayOfMonth = 1,
    months = monthsList(LocalContext.current, year = dateNowLocal().year)
)
// endregion