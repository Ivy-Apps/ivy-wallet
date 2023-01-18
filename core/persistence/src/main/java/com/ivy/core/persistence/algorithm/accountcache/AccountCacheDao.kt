package com.ivy.core.persistence.algorithm.accountcache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface AccountCacheDao {
    @Query("SELECT * FROM account_cache WHERE accountId = :accountId LIMIT 1")
    fun findAccountCache(accountId: String): Flow<AccountCacheEntity?>

    @Query("SELECT timestamp FROM account_cache WHERE accountId = :accountId LIMIT 1")
    suspend fun findTimestampById(accountId: String): Instant?

    @Upsert
    suspend fun save(cache: AccountCacheEntity)

    @Query("DELETE FROM account_cache WHERE accountId = :accountId")
    suspend fun delete(accountId: String)

    @Query("DELETE FROM account_cache")
    suspend fun deleteAll()
}