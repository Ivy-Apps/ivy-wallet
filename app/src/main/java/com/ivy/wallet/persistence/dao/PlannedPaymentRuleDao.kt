package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.PlannedPaymentRule
import java.util.*

@Dao
interface PlannedPaymentRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: PlannedPaymentRule)

    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 ORDER BY amount DESC, startDate ASC")
    fun findAll(): List<PlannedPaymentRule>

    @Query("SELECT * FROM planned_payment_rules WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<PlannedPaymentRule>

    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 AND oneTime = :oneTime ORDER BY amount DESC, startDate ASC")
    fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRule>

    @Query("SELECT * FROM planned_payment_rules WHERE id = :id AND isDeleted = 0")
    fun findById(id: UUID): PlannedPaymentRule?

    @Query("UPDATE planned_payment_rules SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("UPDATE planned_payment_rules SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    fun flagDeletedByAccountId(accountId: UUID)

    @Query("DELETE FROM planned_payment_rules WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM planned_payment_rules")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM planned_payment_rules WHERE isDeleted = 0 ")
    fun countPlannedPayments(): Long
}