package com.ivy.sync.account

import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.wallet.io.network.data.AccountDTO

fun Account.mark(
    isSynced: Boolean,
    isDeleted: Boolean
): Account = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = isDeleted,
        )
    )
)

fun mapToDTO(acc: Account): AccountDTO = AccountDTO(
    id = acc.id,
    name = acc.name,
    currency = acc.currencyCode,
    color = acc.color,
    icon = acc.icon,
    orderNum = acc.metadata.orderNum,
    includeInBalance = !acc.excluded,
)