package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.Account
import java.util.*

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<Account>)

    @Query("SELECT * FROM accounts WHERE isDeleted = 0 ORDER BY orderNum ASC")
    fun findAll(): List<Account>

    @Query("SELECT * FROM accounts WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<Account>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun findById(id: UUID): Account?

    @Query("SELECT * FROM accounts WHERE seAccountId = :seAccountId")
    fun findBySeAccountId(seAccountId: String): Account?

    @Query("UPDATE accounts SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM accounts WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM accounts")
    fun deleteAll()

    @Query("SELECT MIN(orderNum) FROM accounts")
    fun findMinOrderNum(): Double

    @Query("SELECT MAX(orderNum) FROM accounts")
    fun findMaxOrderNum(): Double
}