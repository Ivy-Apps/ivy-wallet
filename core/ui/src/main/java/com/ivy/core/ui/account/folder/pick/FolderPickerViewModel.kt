package com.ivy.core.ui.account.folder.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FolderPickerViewModel @Inject constructor(
    accountsFoldersFlow: AccountFoldersFlow,
    private val mapFolderUiAct: MapFolderUiAct
) : SimpleFlowViewModel<FolderPickerState, Unit>() {
    override val initialUi = FolderPickerState(folders = emptyList())

    override val uiFlow: Flow<FolderPickerState> =
        accountsFoldersFlow(Unit).map { accountsFolders ->
            FolderPickerState(
                folders = accountsFolders
                    .filterIsInstance<AccountListItem.FolderWithAccounts>()
                    .map { mapFolderUiAct(it.accountFolder) }
            )
        }

    // region Event Handling
    override suspend fun handleEvent(event: Unit) {}
    // endregion
}