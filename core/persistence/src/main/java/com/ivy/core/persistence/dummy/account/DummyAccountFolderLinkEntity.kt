package com.ivy.core.persistence.dummy.account

import com.ivy.core.persistence.entity.account.AccountFolderLinkEntity
import com.ivy.data.SyncState
import java.util.*

fun dummyAccountFolderLinkEntity(
    id: String = UUID.randomUUID().toString(),
    folderId: String = UUID.randomUUID().toString(),
    accountId: String = UUID.randomUUID().toString(),
    sync: SyncState = SyncState.Synced,
) = AccountFolderLinkEntity(
    id = id,
    folderId = folderId,
    accountId = accountId,
    sync = sync,
)