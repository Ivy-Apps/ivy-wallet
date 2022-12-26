package com.ivy.core.ui.account.pick

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.SingleAccountPickerModal(
    modal: IvyModal,
    level: Int = 1,
    selected: AccountUi,
    onSelectAccount: (AccountUi) -> Unit,
) {
    val createAccountModal = rememberIvyModal()

    AccountPickerModal(
        modal = modal,
        level = level,
        selected = listOf(selected),
        deselectButton = false,
        onSelectAccount = {
            onSelectAccount(it)
            modal.hide()
        },
        onDeselectAccount = {
            // do nothing
        }
    )

    CreateAccountModal(
        modal = createAccountModal,
        level = level + 1,
    )
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        SingleAccountPickerModal(
            modal = modal,
            selected = dummyAccountUi(),
            onSelectAccount = {}
        )
    }
}