package com.ivy.core.ui.account.folder

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.pick.AccountPickerColumn
import com.ivy.core.ui.color.ColorButton
import com.ivy.core.ui.color.picker.ColorPickerModal
import com.ivy.core.ui.component.ItemIconNameRow
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.data.ItemIconId
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun BoxScope.BaseFolderModal(
    modal: IvyModal,
    level: Int,
    autoFocusNameInput: Boolean,
    title: String,
    positiveButtonText: String,
    secondaryActions: (@Composable ModalActionsScope.() -> Unit)? = null,
    initialName: String,
    icon: ItemIcon,
    color: Color,
    accounts: List<AccountUi>,
    onNameChane: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onIconChange: (ItemIconId) -> Unit,
    onAccountsChange: (List<AccountUi>) -> Unit,
    onSave: (SaveFolderInfo) -> Unit,
) {
    val iconPickerModal = rememberIvyModal()
    val colorPickerModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    Modal(
        modal = modal,
        level = level,
        actions = {
            secondaryActions?.invoke(this)
            Positive(
                text = positiveButtonText,
                feeling = Feeling.Custom(color)
            ) {
                onSave(SaveFolderInfo(color))
                keyboardController?.hide()
                modal.hide()
            }
        }
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item(key = "modal_title") {
                Title(text = title)
                SpacerVer(height = 24.dp)
            }
            item(key = "icon_name_color") {
                // Keep in one item because so the title
                // won't disappear on scroll
                ItemIconNameRow(
                    icon = icon,
                    color = color,
                    initialName = initialName,
                    nameInputHint = "Folder name",
                    autoFocusInput = autoFocusNameInput,
                    onPickIcon = {
                        keyboardController?.hide()
                        iconPickerModal.show()
                    },
                    onNameChange = onNameChane
                )
                SpacerVer(height = 16.dp)
                ColorButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = color
                ) {
                    keyboardController?.hide()
                    colorPickerModal.show()
                }
                SpacerVer(height = 24.dp)
            }
            item(key = "accounts_in_folder") {
                // Can't have create account modal
                // because of infinite recursion
                AccountsInFolder(
                    selected = accounts,
                    createAccountModal = null,
                    onSelectedChange = onAccountsChange
                )
            }
        }
    }

    IconPickerModal(
        modal = iconPickerModal,
        level = level + 1,
        initialIcon = icon,
        color = color,
        onIconPick = onIconChange,
    )

    ColorPickerModal(
        modal = colorPickerModal,
        level = level + 1,
        initialColor = color,
        onColorPicked = onColorChange,
    )
}

data class SaveFolderInfo(
    val color: Color,
)

@Composable
private fun ColumnScope.AccountsInFolder(
    selected: List<AccountUi>,
    createAccountModal: IvyModal?,
    onSelectedChange: (List<AccountUi>) -> Unit,
) {
    DividerHor()
    SpacerVer(height = 12.dp)
    B1(
        modifier = Modifier.padding(start = 24.dp),
        text = "Accounts in folder",
        fontWeight = FontWeight.ExtraBold
    )
    SpacerVer(height = 12.dp)
    AccountPickerColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        selected = selected,
        deselectButton = true,
        onAddAccount = null,
        onSelectAccount = {
            onSelectedChange(selected.plus(it))
        },
        onDeselectAccount = { deselected ->
            onSelectedChange(selected.filter { it.id != deselected.id })
        }
    )
    SpacerVer(height = 24.dp)
    DividerHor()
    SpacerVer(height = 48.dp)
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        BaseFolderModal(
            modal = modal,
            level = 1,
            autoFocusNameInput = false,
            title = "New folder",
            positiveButtonText = "Add folder",
            initialName = "",
            icon = dummyIconUnknown(R.drawable.ic_vue_files_folder),
            color = Purple,
            accounts = listOf(),
            onNameChane = {},
            onColorChange = {},
            onIconChange = {},
            onAccountsChange = {},
            onSave = {},
        )
    }
}
// endregion