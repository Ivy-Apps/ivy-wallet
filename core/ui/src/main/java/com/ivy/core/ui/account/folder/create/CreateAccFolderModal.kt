package com.ivy.core.ui.account.folder.create

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.color.ColorButton
import com.ivy.core.ui.color.picker.ColorPickerModal
import com.ivy.core.ui.components.ItemIconNameRow
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.CreateAccFolderModal(modal: IvyModal) {
    val viewModel: CreateAccFolderViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    val primary = UI.colors.primary
    var folderColor by remember(primary) { mutableStateOf(primary) }

    val iconPickerModal = rememberIvyModal()
    val colorPickerModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    Modal(
        modal = modal,
        actions = {
            Positive(
                text = "Add folder",
                feeling = Feeling.Custom(folderColor)
            ) {
                viewModel?.onEvent(
                    CreateAccFolderEvent.CreateFolder(
                        color = folderColor,
                    )
                )
                keyboardController?.hide()
                modal.hide()
            }
        }
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item(key = "title") {
                Title(text = "New account folder")
                SpacerVer(height = 24.dp)
            }
            item(key = "item_icon_name_row") {
                ItemIconNameRow(
                    icon = state.icon,
                    color = folderColor,
                    initialName = "",
                    nameInputHint = "Folder name",
                    onPickIcon = {
                        keyboardController?.hide()
                        iconPickerModal.show()
                    },
                    onNameChange = { viewModel?.onEvent(CreateAccFolderEvent.NameChange(it)) }
                )
                SpacerVer(height = 16.dp)
            }
            item(key = "color_button") {
                ColorButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = folderColor
                ) {
                    keyboardController?.hide()
                    colorPickerModal.show()
                }
                SpacerVer(height = 16.dp)
            }
            item(key = "last_item_spacer") {
                SpacerVer(height = 48.dp) // last spacer
            }
        }
    }

    IconPickerModal(
        modal = iconPickerModal,
        initialIcon = state.icon,
        color = folderColor,
        onIconPick = { viewModel?.onEvent(CreateAccFolderEvent.IconChange(it)) }
    )

    ColorPickerModal(
        modal = colorPickerModal,
        initialColor = folderColor,
        onColorPicked = { folderColor = it }
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