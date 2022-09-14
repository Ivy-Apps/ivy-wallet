package com.ivy.data.transaction

import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import java.util.*

data class Transaction(
    val id: UUID,

    val account: Account,
    val type: TrnType,
    val value: Value,
    val category: Category?,
    val time: TrnTime,
    val tags: List<Tag>,

    val title: String?,
    val description: String?,

    val attachmentUrl: String?,

    val state: TrnState,
    val purpose: TrnPurpose?,
    val sync: SyncState,
    val metadata: TrnMetadata
)