package com.ivy.core.ui.account.folder.create

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.account.folder.BaseFolderModal
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.CreateAccFolderModal(
    modal: IvyModal,
    level: Int = 1,
) {
    val viewModel: CreateAccFolderViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    val primary = UI.colors.primary
    var folderColor by remember(primary) { mutableStateOf(primary) }

    BaseFolderModal(
        modal = modal,
        level = level,
        title = "New folder",
        positiveButtonText = "Add folder",
        initialName = "",
        icon = state.icon,
        color = folderColor,
        onNameChane = { viewModel?.onEvent(CreateAccFolderEvent.NameChange(it)) },
        onColorChange = { folderColor = it },
        onIconChange = { viewModel?.onEvent(CreateAccFolderEvent.IconChange(it)) },
        onSave = {
            viewModel?.onEvent(CreateAccFolderEvent.CreateFolder(folderColor))
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
        CreateAccFolderModal(modal = modal)
    }
}

private fun previewState() = CreateAccFolderState(
    icon = ItemIcon.Unknown(
        icon = R.drawable.ic_vue_files_folder,
        iconId = "ic_vue_files_folder",
    )
)
// endregion