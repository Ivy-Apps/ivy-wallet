package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.account.Folder
import javax.inject.Inject

class FolderAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao
) : Action<String, Folder?>() {
    override suspend fun String.willDo(): Folder? =
        accountFolderDao.findById(this)?.let(::toDomain)
}

fun toDomain(entity: AccountFolderEntity) = Folder(
    id = entity.id,
    name = entity.name,
    icon = entity.icon,
    color = entity.color,
    orderNum = entity.orderNum
)