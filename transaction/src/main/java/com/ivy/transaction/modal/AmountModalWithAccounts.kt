package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.dummy.dummyValue
import com.ivy.core.ui.account.pick.SingleAccountPickerRow
import com.ivy.core.ui.amount.AmountModal
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.data.Value
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.AmountModalWithAccounts(
    modal: IvyModal,
    amount: Value?,
    account: AccountUi,
    level: Int = 1,
    key: String? = null,
    onAddAccount: () -> Unit,
    onAmountEnter: (Value) -> Unit,
    onAccountChange: (AccountUi) -> Unit,
) {
    AmountModal(
        modal = modal,
        level = level,
        key = key,
        initialAmount = amount,
        contentAbove = {
            SpacerVer(height = 24.dp)
            SingleAccountPickerRow(
                selected = account,
                onAddAccount = onAddAccount,
                onSelectedChange = onAccountChange
            )
            SpacerVer(height = 12.dp)
        },
        onAmountEnter = onAmountEnter
    )
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        AmountModalWithAccounts(
            modal = modal,
            amount = dummyValue(),
            account = dummyAccountUi(),
            onAddAccount = {},
            onAmountEnter = {},
            onAccountChange = {}
        )
    }
}