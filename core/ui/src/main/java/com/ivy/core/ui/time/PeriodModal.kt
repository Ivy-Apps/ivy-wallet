package com.ivy.core.ui.time

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.time.handling.PeriodModalEvent
import com.ivy.core.ui.time.handling.SelectedPeriodHandlerViewModel
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Set

@Composable
fun BoxScope.PeriodModal(
    modal: IvyModal,
    selectedPeriod: SelectedPeriodUi
) {
    val viewModel: SelectedPeriodHandlerViewModel = viewModel()

    var internalPeriod by remember(selectedPeriod) {
        mutableStateOf(selectedPeriod)
    }

    Modal(
        modal = modal,
        actions = {
            Set {
                viewModel.onEvent(PeriodModalEvent.SetPeriod(internalPeriod))
            }
        }
    ) {
        val setSelected = { newPeriod: SelectedPeriodUi ->
            internalPeriod = newPeriod
        }
        ChooseMonth(selected = internalPeriod, setSelected = setSelected)
        FromToRange(selected = internalPeriod, setSelected = setSelected)
        InTheLast(selected = internalPeriod, setSelected = setSelected)
        AllTime(selected = internalPeriod, setSelected = setSelected)
    }
}

@Composable
private fun ChooseMonth(
    selected: SelectedPeriodUi,
    setSelected: (SelectedPeriodUi) -> Unit
) {

}

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