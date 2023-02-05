package com.ivy.data.transaction

import com.ivy.data.Sync
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import java.util.*

@Deprecated("will be removed!")
data class Transaction(
    val id: UUID,

    val account: Account,
    val type: TransactionType,
    val value: Value,
    val category: Category?,
    val time: TrnTime,

    val title: String?,
    val description: String?,

    val state: TrnState,
    val purpose: TrnPurpose?,
    val tags: List<Tag>,
    val attachments: List<Attachment>,

    val metadata: TrnMetadata,
    val sync: Sync,
)