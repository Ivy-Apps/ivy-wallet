package com.ivy.core.persistence.entity.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import com.ivy.data.account.AccountState
import java.time.Instant


@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currency")
    val currency: String,
    @ColumnInfo(name = "color")
    val color: Int,
    @ColumnInfo(name = "icon")
    val icon: String?,
    @ColumnInfo(name = "folderId", index = true)
    val folderId: String?,
    @ColumnInfo(name = "orderNum", index = true)
    val orderNum: Double,
    @ColumnInfo(name = "excluded")
    val excluded: Boolean,
    @ColumnInfo(name = "state", index = true)
    val state: AccountState,
    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)