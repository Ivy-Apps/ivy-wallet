package com.ivy.core.ui.account.reorder

import arrow.core.Either
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.account.folder.AccountFoldersFlow
import com.ivy.core.domain.action.account.folder.WriteAccountFolderAct
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.account.reorder.data.ReorderAccListItemUi
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.action.mapping.account.MapFolderUiAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.account.AccountFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.ivy.core.ui.account.reorder.ReorderAccountsViewModel.InternalState as InternalState1

@HiltViewModel
internal class ReorderAccountsViewModel @Inject constructor(
    accountFoldersFlow: AccountFoldersFlow,
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapFolderUiAct: MapFolderUiAct,
    private val writeAccountFolderAct: WriteAccountFolderAct,
    private val writeAccountsAct: WriteAccountsAct,
    private val timeProvider: TimeProvider,
) : FlowViewModel<InternalState1, ReorderAccountsStateUi, ReorderAccountsEvent>() {
    override val initialState = InternalState(
        items = emptyList(),
    )

    override val initialUi = ReorderAccountsStateUi(
        items = emptyList(),
    )

    override val stateFlow: Flow<InternalState> = accountFoldersFlow(Unit).map { items ->
        InternalState(
            items = items,
        )
    }

    override val uiFlow: Flow<ReorderAccountsStateUi> = stateFlow
        .map { internalState ->
            internalState.items.flatMap {
                when (it) {
                    is AccountListItem.AccountHolder -> listOf(
                        ReorderAccListItemUi.AccountHolder(
                            account = mapAccountUiAct(it.account)
                        )
                    )
                    is AccountListItem.Archived -> it.accounts.toReorderAccListItems()
                    is AccountListItem.FolderWithAccounts -> listOf(
                        ReorderAccListItemUi.FolderHolder(
                            folder = mapFolderUiAct(it.accountFolder)
                        )
                    ) + it.accounts.toReorderAccListItems() + listOf(
                        ReorderAccListItemUi.FolderEnd
                    )
                }
            }
        }.map { items ->
            ReorderAccountsStateUi(
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

    private suspend fun handleReorder(event: ReorderAccountsEvent.Reorder) {
        val internalItems = state.value.items
        val accounts = internalItems.flatMap {
            when (it) {
                is AccountListItem.AccountHolder -> listOf(it.account)
                is AccountListItem.Archived -> it.accounts
                is AccountListItem.FolderWithAccounts -> it.accounts
            }
        }
        val folders = internalItems.filterIsInstance<AccountListItem.FolderWithAccounts>()
            .map { it.accountFolder }

        val reordered = event.reordered.mapIndexedNotNull { index, item ->
            // TODO: Optimize this expensive search logic with a map
            when (item) {
                is ReorderAccListItemUi.AccountHolder ->
                    accounts.firstOrNull { it.id.toString() == item.account.id }
                        ?.copy(orderNum = index.toDouble())
                        ?.let { Either.Left(it) }
                is ReorderAccListItemUi.FolderHolder ->
                    folders.firstOrNull { it.id == item.folder.id }
                        ?.copy(orderNum = index.toDouble())
                        ?.let { Either.Right(it) }
                ReorderAccListItemUi.FolderEnd -> null
            }
        }

        val expectedCount = uiState.value.items.count {
            when (it) {
                is ReorderAccListItemUi.AccountHolder -> true
                is ReorderAccListItemUi.FolderHolder -> true
                ReorderAccListItemUi.FolderEnd -> false
            }
        }
        // verify no lost of data
        if (reordered.size == expectedCount) {
            val accountsToUpdate = reordered.filterIsInstance<Either.Left<Account>>()
                .map { it.value }
                .map {
                    it.copy(
                        sync = Sync(
                            state = SyncState.Syncing,
                            lastUpdated = timeProvider.timeNow(),
                        )
                    )
                }
            writeAccountsAct(Modify.saveMany(accountsToUpdate))
            val foldersToUpdate = reordered.filterIsInstance<Either.Right<AccountFolder>>()
                .map { it.value }
                .map {
                    it.copy(
                        sync = Sync(
                            state = SyncState.Syncing,
                            lastUpdated = timeProvider.timeNow(),
                        )
                    )
                }
            writeAccountFolderAct(Modify.saveMany(foldersToUpdate))
        }
    }

    // endregion

    data class InternalState(
        val items: List<AccountListItem>,
    )
}