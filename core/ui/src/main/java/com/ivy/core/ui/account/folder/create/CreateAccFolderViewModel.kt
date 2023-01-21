package com.ivy.core.ui.account.folder.create

import androidx.compose.ui.graphics.toArgb
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.NewAccountTabItemOrderNumAct
import com.ivy.core.domain.action.account.folder.WriteAccountFolderAct
import com.ivy.core.domain.action.account.folder.WriteAccountFolderContentAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.AccountFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
internal class CreateAccFolderViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeAccountFolderAct: WriteAccountFolderAct,
    private val writeAccountFolderContentAct: WriteAccountFolderContentAct,
    private val newAccountTabItemOrderNumAct: NewAccountTabItemOrderNumAct,
    private val timeProvider: TimeProvider,
) : SimpleFlowViewModel<CreateAccFolderState, CreateAccFolderEvent>() {
    override val initialUi = CreateAccFolderState(
        icon = ItemIcon.Unknown(
            icon = R.drawable.ic_vue_files_folder,
            iconId = "ic_vue_files_folder",
        )
    )

    private var folderName = ""
    private val folderIconId = MutableStateFlow<ItemIconId?>(null)

    override val uiFlow: Flow<CreateAccFolderState> = folderIconId.map { iconId ->
        CreateAccFolderState(
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Folder))
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: CreateAccFolderEvent) = when (event) {
        is CreateAccFolderEvent.CreateFolder -> handleCreateFolder(event)
        is CreateAccFolderEvent.NameChange -> handleFolderNameChange(event)
        is CreateAccFolderEvent.IconChange -> handleIconChange(event)
    }

    private suspend fun handleCreateFolder(event: CreateAccFolderEvent.CreateFolder) {
        val newAccountFolder = AccountFolder(
            id = UUID.randomUUID().toString(),
            name = folderName,
            icon = folderIconId.value,
            color = event.color.toArgb(),
            orderNum = newAccountTabItemOrderNumAct(Unit),
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )
        writeAccountFolderAct(Modify.save(newAccountFolder))
        writeAccountFolderContentAct(
            WriteAccountFolderContentAct.Input(
                folderId = newAccountFolder.id,
                accountIds = event.accounts.map { it.id }
            )
        )
    }

    private fun handleFolderNameChange(event: CreateAccFolderEvent.NameChange) {
        folderName = event.name
    }

    private fun handleIconChange(event: CreateAccFolderEvent.IconChange) {
        folderIconId.value = event.iconId
    }
    // endregion
}