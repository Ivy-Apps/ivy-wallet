package com.ivy.core.persistence.algorithm.accountcache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "account_cache")
data class AccountCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "accountId", index = true)
    val accountId: String,
    @ColumnInfo(name = "incomesJson")
    val incomesJson: String,
    @ColumnInfo(name = "expensesJson")
    val expensesJson: String,
    @ColumnInfo(name = "incomesCount")
    val incomesCount: Int,
    @ColumnInfo(name = "expensesCount")
    val expensesCount: Int,
    @ColumnInfo(name = "timestamp")
    val timestamp: Instant,
)