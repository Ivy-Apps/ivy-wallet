package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.dummy.dummyValue
import com.ivy.core.ui.account.pick.SingleAccountPickerRow
import com.ivy.core.ui.amount.AmountModal
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.data.Value
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.TransferAmountModal(
    modal: IvyModal,
    level: Int = 1,
    amount: Value?,
    fromAccount: AccountUi,
    toAccount: AccountUi,
    onAddAccount: () -> Unit,
    onAmountEnter: (Value) -> Unit,
    onFromAccountChange: (AccountUi) -> Unit,
    onToAccountChange: (AccountUi) -> Unit,
) {
    AmountModal(
        modal = modal,
        level = level,
        initialAmount = amount,
        contentAbove = {
            SpacerVer(height = 16.dp)
            B2(
                modifier = Modifier.padding(start = 32.dp),
                text = "From",
                fontWeight = FontWeight.SemiBold
            )
            SpacerVer(height = 4.dp)
            SingleAccountPickerRow(
                selected = fromAccount,
                onAddAccount = onAddAccount,
                onSelectedChange = onFromAccountChange
            )
            SpacerVer(height = 8.dp)
            B2(
                modifier = Modifier.padding(start = 32.dp),
                text = "To",
                fontWeight = FontWeight.SemiBold
            )
            SpacerVer(height = 4.dp)
            SingleAccountPickerRow(
                selected = toAccount,
                onAddAccount = onAddAccount,
                onSelectedChange = onToAccountChange
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
        TransferAmountModal(
            modal = modal,
            amount = dummyValue(),
            fromAccount = dummyAccountUi(),
            toAccount = dummyAccountUi(),
            onAddAccount = {},
            onAmountEnter = {},
            onFromAccountChange = {},
            onToAccountChange = {}
        )
    }
}