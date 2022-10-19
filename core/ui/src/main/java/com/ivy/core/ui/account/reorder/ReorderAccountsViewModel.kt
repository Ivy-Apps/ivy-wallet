package com.ivy.core.ui.account.reorder

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.ui.account.reorder.data.ReorderAccListItemUi
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import com.ivy.data.account.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ReorderAccountsViewModel @Inject constructor(
    accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapFolderUiAct: MapFolderUiAct,
) : SimpleFlowViewModel<ReorderAccountsState, ReorderAccountsEvent>() {
    override val initialUi = ReorderAccountsState(
        items = listOf(),
    )

    override val uiFlow: Flow<ReorderAccountsState> = accountFoldersFlow(Unit)
        .map { items ->
            items.flatMap {
                when (it) {
                    is AccountListItem.AccountHolder -> listOf(
                        ReorderAccListItemUi.AccountHolder(
                            account = mapAccountUiAct(it.account)
                        )
                    )
                    is AccountListItem.Archived -> it.accounts.toReorderAccListItems()
                    is AccountListItem.FolderWithAccounts -> listOf(
                        ReorderAccListItemUi.FolderHolder(
                            folder = mapFolderUiAct(it.folder)
                        )
                    ) + it.accounts.toReorderAccListItems()
                }
            }
        }.map { items ->
            ReorderAccountsState(
                items = items,
            )
        }

    private suspend fun List<Account>.toReorderAccListItems(): List<ReorderAccListItemUi> =
        this.map {
            ReorderAccListItemUi.AccountHolder(
                account = mapAccountUiAct(it)
            )
        }

    // region Event handling
    override suspend fun handleEvent(event: ReorderAccountsEvent) = when (event) {
        is ReorderAccountsEvent.Reorder -> handleReorder(event)
    }

    private fun handleReorder(event: ReorderAccountsEvent.Reorder) {
        // TODO: Implement
    }
    // endregion
}