package com.ivy.core.ui.account.pick

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.AccountPickerModal(
    modal: IvyModal,
    level: Int = 1,
    selected: List<AccountUi>,
    deselectButton: Boolean,
    onSelectAccount: (AccountUi) -> Unit,
    onDeselectAccount: (AccountUi) -> Unit,
) {
    val createAccountModal = rememberIvyModal()

    Modal(
        modal = modal,
        level = level,
        actions = {
            // no actions
        }
    ) {
        LazyColumn(
            modifier = Modifier
        ) {
            item(key = "title") {
                Title(
                    text = stringResource(id = R.string.account),
                    paddingStart = 24.dp,
                )
                SpacerVer(height = 16.dp)
            }
            item(key = "accounts") {
                AccountPickerColumn(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    selected = selected,
                    deselectButton = deselectButton,
                    onAddAccount = {
                        createAccountModal.show()
                    },
                    onSelectAccount = onSelectAccount,
                    onDeselectAccount = onDeselectAccount
                )
            }
            item(key = "last_item_spacer") {
                SpacerVer(height = 24.dp)
            }
        }
    }

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
        AccountPickerModal(
            modal = modal,
            selected = emptyList(),
            deselectButton = false,
            onSelectAccount = {},
            onDeselectAccount = {},
        )
    }
}