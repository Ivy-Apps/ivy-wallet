package com.ivy.home.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R

@Composable
internal fun BoxScope.AddTransactionModal(
    modal: IvyModal,
    onAddTransfer: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
) {
    Modal(
        modal = modal,
        actions = {}
    ) {
        Title(text = "Add transaction")
        SpacerVer(height = 24.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = stringResource(R.string.transfer),
            icon = R.drawable.ic_transfer,
            onClick = {
                modal.hide()
                onAddTransfer()
            }
        )
        SpacerVer(height = 12.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Custom(UI.colors.green),
            text = stringResource(R.string.income),
            icon = R.drawable.ic_income,
            onClick = {
                modal.hide()
                onAddIncome()
            }
        )
        SpacerVer(height = 12.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Custom(UI.colors.red),
            text = stringResource(R.string.expense),
            icon = R.drawable.ic_expense,
            onClick = {
                modal.hide()
                onAddExpense()
            }
        )
        SpacerVer(height = 16.dp)
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        AddTransactionModal(
            modal = modal,
            onAddTransfer = {},
            onAddIncome = {},
            onAddExpense = {},
        )
    }
}