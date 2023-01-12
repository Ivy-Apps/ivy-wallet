package com.ivy.backup.base

import com.ivy.data.account.Account
import com.ivy.data.account.Folder
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import java.util.*

data class BackupData(
    // region Core data
    val accounts: List<Account>,
    val categories: List<Category>,
    val transactions: List<Transaction>,
    val transfers: List<BatchTransferData>,
    // endregion

    // region Ivy New data
    val accountFolders: Map<Folder, List<UUID>>?,
    val tags: List<Tag>?,
    val attachments: List<Attachment>?,
    // endregion

    val settings: SettingsData,
)