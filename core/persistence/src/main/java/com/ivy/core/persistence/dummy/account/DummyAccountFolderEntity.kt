package com.ivy.core.persistence.dummy.account

import com.ivy.core.persistence.entity.account.AccountFolderEntity
import com.ivy.data.SyncState
import java.util.*

fun dummyAccountFolderEntity(
    id: String = UUID.randomUUID().toString(),
    name: String = "Folder",
    color: Int = 123123,
    icon: String? = "icon",
    orderNum: Double = 0.0,
    sync: SyncState = SyncState.Synced
): AccountFolderEntity = AccountFolderEntity(
    id = id,
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    sync = sync
)