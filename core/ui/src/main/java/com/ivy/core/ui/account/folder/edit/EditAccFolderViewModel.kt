package com.ivy.core.ui.account.folder.edit

import androidx.compose.ui.graphics.toArgb
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.FolderAct
import com.ivy.core.domain.action.account.folder.WriteAccountFolderAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.data.account.Folder
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class EditAccFolderViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeAccountFolderAct: WriteAccountFolderAct,
    private val folderAct: FolderAct,
) : SimpleFlowViewModel<EditAccFolderState, EditAccFolderEvent>() {
    override val initialUi = EditAccFolderState(
        icon = ItemIcon.Unknown(
            icon = R.drawable.ic_vue_files_folder,
            iconId = "ic_vue_files_folder",
        ),
        color = Purple,
        initialName = "",
        accounts = emptyList(),
    )

    private var folder: Folder? = null
    private var folderName = ""
    private val initialName = MutableStateFlow(initialUi.initialName)
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val color = MutableStateFlow(initialUi.color)
    private val accounts = MutableStateFlow(initialUi.accounts)

    override val uiFlow: Flow<EditAccFolderState> = combine(
        initialName, iconId, color, accounts
    ) { initialName, iconId, color, accounts ->
        EditAccFolderState(
            initialName = initialName,
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Folder)),
            color = color,
            accounts = accounts
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: EditAccFolderEvent) = when (event) {
        is EditAccFolderEvent.Initial -> handleInitial(event)
        is EditAccFolderEvent.EditFolder -> handleEditFolder()
        is EditAccFolderEvent.NameChange -> handleFolderNameChange(event)
        is EditAccFolderEvent.IconChange -> handleIconChange(event)
        is EditAccFolderEvent.ColorChange -> handleColorChange(event)
        is EditAccFolderEvent.AccountsChange -> handleAccountsChange(event)
        EditAccFolderEvent.Delete -> handleDelete()
    }

    private suspend fun handleInitial(event: EditAccFolderEvent.Initial) {
        folderAct(event.folderId)?.let {
            folder = it
            folderName = it.name
            initialName.value = it.name
            iconId.value = it.icon
            color.value = it.color.toComposeColor()
            // TODO: Handle accounts
        }
    }

    private suspend fun handleEditFolder() {
        val updated = folder?.copy(
            name = folderName,
            color = color.value.toArgb(),
            icon = iconId.value
        )
        // TODO: Handle accounts
        if (updated != null) {
            writeAccountFolderAct(Modify.save(updated))
        }
    }

    private fun handleFolderNameChange(event: EditAccFolderEvent.NameChange) {
        folderName = event.name
    }

    private fun handleIconChange(event: EditAccFolderEvent.IconChange) {
        iconId.value = event.iconId
    }

    private fun handleColorChange(event: EditAccFolderEvent.ColorChange) {
        color.value = event.color
    }

    private fun handleAccountsChange(event: EditAccFolderEvent.AccountsChange) {
        accounts.value = event.accounts
    }

    private suspend fun handleDelete() {
        folder?.let {
            writeAccountFolderAct(Modify.delete(it.id))
        }
    }
    // endregion
}