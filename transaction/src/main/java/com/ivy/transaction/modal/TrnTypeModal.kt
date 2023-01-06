package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.transaction.feeling
import com.ivy.core.ui.transaction.humanText
import com.ivy.core.ui.transaction.icon
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.TrnTypeModal(
    modal: IvyModal,
    trnType: TransactionType,
    level: Int = 1,
    onTransactionTypeChange: (TransactionType) -> Unit,
) {
    var selectedTrnType by remember(trnType) {
        mutableStateOf(trnType)
    }

    Modal(
        modal = modal,
        level = level,
        actions = {},
    ) {
        val onSelect = { trnType: TransactionType ->
            selectedTrnType = trnType
            onTransactionTypeChange(trnType)
            modal.hide()
        }

        Title(text = "Transaction Type")
        SpacerVer(height = 24.dp)
        TransactionTypeButton(
            trnType = TransactionType.Income,
            selected = selectedTrnType,
            onSelect = onSelect
        )
        SpacerVer(height = 12.dp)
        TransactionTypeButton(
            trnType = TransactionType.Expense,
            selected = selectedTrnType,
            onSelect = onSelect
        )
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun TransactionTypeButton(
    trnType: TransactionType,
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = if (trnType == selected) Visibility.High else Visibility.Medium,
        feeling = trnType.feeling(),
        text = trnType.humanText(),
        icon = trnType.icon()
    ) {
        onSelect(trnType)
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        TrnTypeModal(
            modal = modal,
            trnType = TransactionType.Income,
            onTransactionTypeChange = {}
        )
    }
}