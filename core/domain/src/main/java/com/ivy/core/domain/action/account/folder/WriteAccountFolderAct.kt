package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.SyncState
import com.ivy.data.account.AccountFolder
import javax.inject.Inject

class WriteAccountFolderAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao
) : Action<Modify<AccountFolder>, Unit>() {
    override suspend fun Modify<AccountFolder>.willDo() = when (this) {
        is Modify.Delete -> delete(this.itemIds)
        is Modify.Save -> save(this.items)
    }

    private suspend fun delete(folderIds: List<String>) = folderIds.forEach {
        accountFolderDao.updateSync(folderId = it, sync = SyncState.Deleting)
    }

    private suspend fun save(folders: List<AccountFolder>) {
        accountFolderDao.save(folders.map(::toSyncingEntity))
    }

    private fun toSyncingEntity(domain: AccountFolder) = AccountFolderEntity(
        id = domain.id,
        name = domain.name,
        color = domain.color,
        icon = domain.icon,
        orderNum = domain.orderNum,
        sync = SyncState.Syncing
    )
}