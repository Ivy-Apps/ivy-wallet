package com.ivy.core.domain.action.account.folder

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.SyncState
import com.ivy.data.account.Folder
import javax.inject.Inject

class WriteAccountFolderAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao,
    private val timeProvider: TimeProvider,
) : Action<Modify<Folder>, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(modify: Modify<Folder>) = when (modify) {
        is Modify.Delete -> delete(modify.itemIds)
        is Modify.Save -> save(modify.items)
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
                .map {
                    toEntity(it, timeProvider)
                }
        )
    }

    private fun validate(folder: Folder): Boolean {
        if (folder.name.isBlank()) return false
        return true
    }

    private fun toEntity(
        domain: Folder,
        timeProvider: TimeProvider,
    ) = AccountFolderEntity(
        id = domain.id,
        name = domain.name,
        color = domain.color,
        icon = domain.icon,
        orderNum = domain.orderNum,
        sync = domain.sync.state,
        lastUpdated = domain.sync.lastUpdated.toUtc(timeProvider)
    )
}