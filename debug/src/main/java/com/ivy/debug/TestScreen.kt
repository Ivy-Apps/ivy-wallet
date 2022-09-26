package com.ivy.debug

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.time.PeriodModal
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton

@Composable
fun BoxScope.TestScreen() {
    val viewModel: TestViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val periodModal = rememberIvyModal()

    ColumnRoot {
        SpacerVer(height = 24.dp)
        IvyButton(
            size = ButtonSize.Big,
            visibility = ButtonVisibility.Focused,
            feeling = ButtonFeeling.Positive,
            text = "Show period modal",
            icon = null
        ) {
            periodModal.show()
        }
    }

    PeriodModal(modal = periodModal, selectedPeriod = state.selectedPeriodUi)
}