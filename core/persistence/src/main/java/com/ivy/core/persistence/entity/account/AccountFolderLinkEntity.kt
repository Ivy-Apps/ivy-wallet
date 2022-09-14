package com.ivy.core.persistence.entity.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState

@Entity(tableName = "account_folder_links")
data class AccountFolderLinkEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    @ColumnInfo(name = "folderId", index = true)
    val folderId: String,
    @ColumnInfo(name = "accountId", index = true)
    val accountId: String,

    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
)