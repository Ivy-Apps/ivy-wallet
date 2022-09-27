package com.ivy.debug

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.time.PeriodButton
import com.ivy.core.ui.time.PeriodModal
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.rememberIvyModal

@Composable
fun BoxScope.TestScreen() {
    val viewModel: TestViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val periodModal = rememberIvyModal()

    ColumnRoot {
        SpacerVer(height = 24.dp)
        Row {
            SpacerWeight(weight = 1f)
            PeriodButton(selectedPeriod = state.selectedPeriodUi, periodModal = periodModal)
            SpacerHor(width = 16.dp)
        }
    }

    PeriodModal(modal = periodModal, selectedPeriod = state.selectedPeriodUi)
}