package com.ivy.backup.base

import com.ivy.data.account.Account
import com.ivy.data.account.Folder
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnListItem
import java.util.*

data class IvyWalletBackup(
    // region Core data
    val accounts: List<Account>,
    val categories: List<Category>,
    val transactions: List<Transaction>,
    val transfers: List<TrnListItem.Transfer>,
    // endregion

    val accountFolders: Map<Folder, List<UUID>>?,
    val tags: List<Tag>?,
    val attachments: List<Attachment>?,
)