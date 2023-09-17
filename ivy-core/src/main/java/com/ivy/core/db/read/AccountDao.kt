package com.ivy.core.db.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.core.db.entity.AccountEntity
import java.util.*

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun findById(id: UUID): AccountEntity?

    @Query("SELECT MIN(orderNum) FROM accounts")
    suspend fun findMinOrderNum(): Double

    @Query("SELECT MAX(orderNum) FROM accounts")
    suspend fun findMaxOrderNum(): Double?
}
