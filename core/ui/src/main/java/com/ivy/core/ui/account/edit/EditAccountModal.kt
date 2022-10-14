package com.ivy.core.ui.account.edit

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.BaseAccountModal
import com.ivy.core.ui.account.edit.components.DeleteAccountModal
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ArchiveButton
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.EditAccountModal(
    modal: IvyModal,
    accountId: String,
    level: Int = 1,
) {
    val viewModel: EditAccountViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(accountId) {
        viewModel?.onEvent(EditAccountEvent.Initial(accountId))
    }

    val deleteAccountModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    BaseAccountModal(
        modal = modal,
        level = level,
        title = stringResource(R.string.edit_account),
        nameInputHint = stringResource(R.string.account_name),
        positiveActionText = stringResource(R.string.save),
        secondaryActions = {
            ArchiveButton(
                archived = state.archived,
                color = state.color,
                onArchive = {
                    keyboardController?.hide()
                    modal.hide()
                    viewModel?.onEvent(EditAccountEvent.Archive)
                },
                onUnarchive = {
                    keyboardController?.hide()
                    modal.hide()
                    viewModel?.onEvent(EditAccountEvent.Unarchive)
                }
            )
            SpacerHor(width = 8.dp)
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.High,
                feeling = Feeling.Negative,
                text = null,
                icon = R.drawable.outline_delete_24
            ) {
                keyboardController?.hide()
                deleteAccountModal.show()
            }
            SpacerHor(width = 12.dp)
        },
        icon = state.icon,
        initialName = state.initialName,
        currency = state.currency,
        color = state.color,
        excluded = state.excluded,
        folder = state.folder,
        contentBelow = {
            item(key = "adjust_balance") {
                SpacerVer(height = 12.dp)
                AdjustBalance(color = state.color) {
                    // TODO: Implement
                }
            }
        },
        onNameChange = { viewModel?.onEvent(EditAccountEvent.NameChange(it)) },
        onIconChange = { viewModel?.onEvent(EditAccountEvent.IconChange(it)) },
        onCurrencyChange = { viewModel?.onEvent(EditAccountEvent.CurrencyChange(it)) },
        onFolderChange = { viewModel?.onEvent(EditAccountEvent.FolderChange(it)) },
        onExcludedChange = { viewModel?.onEvent(EditAccountEvent.ExcludedChange(it)) },
        onColorChange = { viewModel?.onEvent(EditAccountEvent.ColorChange(it)) },
        onSaveAccount = { viewModel?.onEvent(EditAccountEvent.EditAccount) }
    )

    DeleteAccountModal(
        modal = deleteAccountModal,
        level = level + 1,
        accountName = state.initialName,
        onArchive = {
            keyboardController?.hide()
            modal.hide()
            viewModel?.onEvent(EditAccountEvent.Archive)
        },
        onDelete = {
            keyboardController?.hide()
            modal.hide()
            viewModel?.onEvent(EditAccountEvent.Delete)
        }
    )
}

@Composable
private fun AdjustBalance(
    color: Color,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Custom(color),
        text = stringResource(R.string.adjust_balance),
        icon = R.drawable.ic_vue_money_coins,
        onClick = onClick
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        EditAccountModal(
            modal = modal,
            accountId = ""
        )
    }
}

private fun previewState() = EditAccountState(
    currency = "USD",
    icon = dummyIconSized(R.drawable.ic_custom_account_m),
    initialName = "Account",
    folder = null,
    excluded = false,
    color = Purple,
    archived = false,
)
// endregion