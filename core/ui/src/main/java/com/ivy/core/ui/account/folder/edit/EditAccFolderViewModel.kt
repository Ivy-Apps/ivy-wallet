package com.ivy.core.ui.account.folder.edit

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.WriteAccountFolderAct
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.design.l0_system.color.Purple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class EditAccFolderViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeAccountFolderAct: WriteAccountFolderAct,
) : SimpleFlowViewModel<EditAccFolderState, EditAccFolderEvent>() {
    override val initialUi = EditAccFolderState(
        icon = ItemIcon.Unknown(
            icon = R.drawable.ic_vue_files_folder,
            iconId = "ic_vue_files_folder",
        ),
        color = Purple,
        initialName = ""
    )

    private var folderName = ""
    private val initialName = MutableStateFlow(initialUi.initialName)
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val color = MutableStateFlow(initialUi.color)

    override val uiFlow: Flow<EditAccFolderState> = combine(
        initialName, iconId, color
    ) { initialName, iconId, color ->
        EditAccFolderState(
            initialName = initialName,
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Folder)),
            color = color,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: EditAccFolderEvent) = when (event) {
        is EditAccFolderEvent.Initial -> handleInitial(event)
        is EditAccFolderEvent.EditFolder -> handleEditFolder(event)
        is EditAccFolderEvent.NameChange -> handleFolderNameChange(event)
        is EditAccFolderEvent.IconChange -> handleIconChange(event)
        is EditAccFolderEvent.ColorChange -> handleColorChange(event)
        EditAccFolderEvent.Delete -> handleDelete()
    }

    private suspend fun handleInitial(event: EditAccFolderEvent.Initial) {

    }

    private suspend fun handleEditFolder(event: EditAccFolderEvent.EditFolder) {

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

    private suspend fun handleDelete() {

    }
    // endregion
}