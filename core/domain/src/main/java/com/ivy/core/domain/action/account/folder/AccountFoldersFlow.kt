package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.data.AccountListItem
import com.ivy.core.domain.action.data.AccountListItem.AccountHolder
import com.ivy.core.domain.action.data.AccountListItem.FolderHolder
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.account.Account
import com.ivy.data.account.AccountFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private typealias FolderId = String

class AccountFoldersFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accountFolderDao: AccountFolderDao,
) : FlowAction<Unit, List<AccountListItem>>() {
    override fun Unit.createFlow(): Flow<List<AccountListItem>> = combine(
        accountsFlow(), accountFolderDao.findAll()
    ) { accounts, folderEntities ->
        val foldersMap = accounts.groupBy { it.folderId?.toString() ?: "none" }
        val folders = folderEntities.map { FolderHolder(toDomain(foldersMap, it)) }
        val accountsNotInFolder = foldersMap.filterKeys { accFolderId ->
            if (accFolderId == "none") return@filterKeys false
            val folderIds = folders.map { it.folder.id }
            // the referenced folder by the account doesn't exists if:
            !folderIds.contains(accFolderId)
        }.values.flatten()
        val accountHolders = accountsNotInFolder.map(AccountListItem::AccountHolder)

        (folders + accountHolders).sortedBy {
            when (it) {
                is AccountHolder -> it.account.orderNum
                is FolderHolder -> it.folder.orderNum
            }
        }
    }

    private fun toDomain(foldersMap: Map<FolderId, List<Account>>, folder: AccountFolderEntity) =
        AccountFolder(
            id = folder.id,
            name = folder.name,
            icon = folder.icon,
            color = folder.color,
            accounts = foldersMap[folder.id] ?: emptyList(),
            orderNum = folder.orderNum
        )
}