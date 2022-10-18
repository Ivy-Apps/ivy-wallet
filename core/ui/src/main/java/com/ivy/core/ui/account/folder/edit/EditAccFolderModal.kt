package com.ivy.core.ui.account.folder.edit

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.account.folder.BaseFolderModal
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.l3_ivyComponents.modal.DeleteConfirmationModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.resources.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.EditAccFolderModal(
    modal: IvyModal,
    folderId: String,
    level: Int = 1,
) {
    val viewModel: EditAccFolderViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LaunchedEffect(folderId) {
        viewModel?.onEvent(EditAccFolderEvent.Initial(folderId))
    }

    val deleteConfirmationModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    BaseFolderModal(
        modal = modal,
        level = level,
        autoFocusNameInput = false,
        title = "Edit folder",
        positiveButtonText = stringResource(R.string.save),
        secondaryActions = {
            DeleteButton {
                keyboardController?.hide()
                deleteConfirmationModal.show()
            }
            SpacerHor(width = 12.dp)
        },
        initialName = state.initialName,
        icon = state.icon,
        color = state.color,
        accounts = state.accounts,
        onNameChane = { viewModel?.onEvent(EditAccFolderEvent.NameChange(it)) },
        onColorChange = { viewModel?.onEvent(EditAccFolderEvent.ColorChange(it)) },
        onIconChange = { viewModel?.onEvent(EditAccFolderEvent.IconChange(it)) },
        onAccountsChange = { viewModel?.onEvent(EditAccFolderEvent.AccountsChange(it)) },
        onSave = {
            viewModel?.onEvent(EditAccFolderEvent.EditFolder)
        }
    )

    DeleteConfirmationModal(
        modal = deleteConfirmationModal,
        level = level + 1,
    ) {
        modal.hide()
        viewModel?.onEvent(EditAccFolderEvent.Delete)
    }
}

@Composable
private fun DeleteButton(
    onClick: () -> Unit,
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Negative,
        text = null,
        icon = R.drawable.outline_delete_24,
        onClick = onClick,
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        EditAccFolderModal(
            modal = modal,
            folderId = "",
        )
    }
}

private fun previewState() = EditAccFolderState(
    icon = dummyIconUnknown(R.drawable.ic_vue_files_folder),
    color = Purple,
    initialName = "Folder 1",
    accounts = listOf(),
)
// endregion