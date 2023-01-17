package com.ivy.core.persistence.algorithm.accountcache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountCacheDao {
    @Query("SELECT * FROM account_cache WHERE accountId = :accountId LIMIT 1")
    fun findAccountCache(accountId: String): Flow<AccountCacheEntity?>

    @Upsert
    suspend fun save(cache: AccountCacheEntity)
}