package com.ivy.core.ui.account.create

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.account.BaseAccountModal
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.CreateAccountModal(
    modal: IvyModal,
    level: Int = 1
) {
    val viewModel: CreateAccountViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    val primary = UI.colors.primary
    var color by remember(primary) { mutableStateOf(primary) }
    var excluded by remember { mutableStateOf(false) }
    var folder by remember { mutableStateOf<FolderUi?>(null) }

    val newAccountString = stringResource(R.string.new_account)
    BaseAccountModal(
        modal = modal,
        level = level,
        autoFocusNameInput = true,
        title = newAccountString,
        nameInputHint = newAccountString,
        positiveActionText = stringResource(R.string.add_account),
        icon = state.icon,
        initialName = "",
        currency = state.currency,
        color = color,
        excluded = excluded,
        folder = folder,
        onNameChange = { viewModel?.onEvent(CreateAccountEvent.NameChange(it)) },
        onIconChange = { viewModel?.onEvent(CreateAccountEvent.IconChange(it)) },
        onCurrencyChange = { viewModel?.onEvent(CreateAccountEvent.CurrencyChange(it)) },
        onFolderChange = { folder = it },
        onExcludedChange = { excluded = it },
        onColorChange = { color = it },
        onSaveAccount = {
            viewModel?.onEvent(
                CreateAccountEvent.CreateAccount(
                    color = it.color,
                    excluded = it.excluded,
                    folder = it.folder
                )
            )
        }
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CreateAccountModal(modal = modal)
    }
}

private fun previewState() = CreateAccountState(
    currency = "USD",
    icon = dummyIconSized(R.drawable.ic_custom_account_m)
)
// endregion
