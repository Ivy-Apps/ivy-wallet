package com.ivy.core.ui.account.folder.choose

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChooseFolderViewModel @Inject constructor(
    accountsFoldersFlow: AccountFoldersFlow,
    private val mapFolderUiAct: MapFolderUiAct
) : SimpleFlowViewModel<ChooseFolderState, Unit>() {
    override val initialUi = ChooseFolderState(folders = emptyList())

    override val uiFlow: Flow<ChooseFolderState> =
        accountsFoldersFlow(Unit).map { accountsFolders ->
            ChooseFolderState(
                folders = accountsFolders
                    .filterIsInstance<AccountListItem.FolderWithAccounts>()
                    .map { mapFolderUiAct(it.folder) }
            )
        }

    // region Event Handling
    override suspend fun handleEvent(event: Unit) {}
    // endregion
}