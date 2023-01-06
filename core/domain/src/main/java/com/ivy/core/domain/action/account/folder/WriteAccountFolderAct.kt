package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.SyncState
import com.ivy.data.account.Folder
import javax.inject.Inject

class WriteAccountFolderAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao
) : Action<Modify<Folder>, Unit>() {
    override suspend fun Modify<Folder>.willDo() = when (this) {
        is Modify.Delete -> delete(this.itemIds)
        is Modify.Save -> save(this.items)
    }

    private suspend fun delete(folderIds: List<String>) = folderIds.forEach {
        accountFolderDao.updateSync(folderId = it, sync = SyncState.Deleting)
    }

    private suspend fun save(folders: List<Folder>) {
        accountFolderDao.save(
            folders.filter(::validate)
                .map {
                    it.copy(name = it.name.trim())
                }
                .map(::toSyncingEntity)
        )
    }

    private fun validate(folder: Folder): Boolean {
        if (folder.name.isBlank()) return false
        return true
    }

    private fun toSyncingEntity(domain: Folder) = AccountFolderEntity(
        id = domain.id,
        name = domain.name,
        color = domain.color,
        icon = domain.icon,
        orderNum = domain.orderNum,
        sync = SyncState.Syncing
    )
}