package com.ivy.core.domain.action.account.folder

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.data.AccountListItem.AccountHolder
import com.ivy.core.domain.action.data.AccountListItem.FolderWithAccounts
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private typealias FolderId = String

class AccountFoldersFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accountFolderDao: AccountFolderDao,
    private val timeProvider: TimeProvider,
) : FlowAction<Unit, List<AccountListItem>>() {
    override fun createFlow(input: Unit): Flow<List<AccountListItem>> = combine(
        accountsFlow(), accountFolderDao.findAll()
    ) { accounts, folderEntities ->
        val archived = AccountListItem.Archived(
            accounts.filter { it.state == AccountState.Archived }.sortedBy { it.orderNum }
        ).takeIf { it.accounts.isNotEmpty() }
        val notArchived = accounts.filter { it.state == AccountState.Default }

        val foldersMap = notArchived.groupBy { it.folderId?.toString() ?: "none" }
        val folders = folderEntities.map { toDomain(foldersMap, it) }
        val accountsNotInFolder = foldersMap.filterKeys { accFolderId ->
            // accounts with folder "none" aren't in any folder
            if (accFolderId == "none") return@filterKeys true
            val folderIds = folders.map { it.accountFolder.id }
            // the referenced folder by the account doesn't exists if:
            !folderIds.contains(accFolderId)
        }.values.flatten()
        val accountHolders = accountsNotInFolder.map(AccountListItem::AccountHolder)

        val result = if (archived != null)
            folders + accountHolders + archived else folders + accountHolders
        result.sortedBy {
            when (it) {
                is AccountHolder -> it.account.orderNum
                is FolderWithAccounts -> it.accountFolder.orderNum
                is AccountListItem.Archived -> Double.MAX_VALUE - 10 // put archived as last
            }
        }
    }

    private fun toDomain(
        foldersMap: Map<FolderId, List<Account>>,
        entity: AccountFolderEntity
    ) = FolderWithAccounts(
        accountFolder = toDomain(entity, timeProvider),
        accounts = foldersMap[entity.id] ?: emptyList(),
    )
}