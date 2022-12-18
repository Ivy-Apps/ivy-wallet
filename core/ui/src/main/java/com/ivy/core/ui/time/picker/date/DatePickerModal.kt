package com.ivy.core.ui.time.picker.date

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import java.time.LocalDate

@Composable
fun BoxScope.DatePickerModal(
    modal: IvyModal,
    selected: LocalDate,
    level: Int = 1,
    onPick: (LocalDate) -> Unit,
) {
    Modal(
        modal = modal,
        level = level,
        actions = {

        }
    ) {
        Title(text = "Pick a date")
        SpacerVer(height = 24.dp)
        val listState = rememberLazyListState()
        LazyColumn {

        }
        SpacerVer(height = 24.dp)
    }
}