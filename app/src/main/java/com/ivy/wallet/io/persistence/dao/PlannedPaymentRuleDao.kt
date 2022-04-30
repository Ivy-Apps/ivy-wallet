package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.PlannedPaymentRuleEntity
import java.util.*

@Dao
interface PlannedPaymentRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: PlannedPaymentRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<PlannedPaymentRuleEntity>)

    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 ORDER BY amount DESC, startDate ASC")
    suspend fun findAll(): List<PlannedPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<PlannedPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 AND oneTime = :oneTime ORDER BY amount DESC, startDate ASC")
    suspend fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE id = :id AND isDeleted = 0")
    suspend fun findById(id: UUID): PlannedPaymentRuleEntity?

    @Query("UPDATE planned_payment_rules SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("UPDATE planned_payment_rules SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    suspend fun flagDeletedByAccountId(accountId: UUID)

    @Query("DELETE FROM planned_payment_rules WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM planned_payment_rules")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM planned_payment_rules WHERE isDeleted = 0 ")
    suspend fun countPlannedPayments(): Long
}