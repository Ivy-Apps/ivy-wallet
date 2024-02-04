package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import java.util.*

@Dao
interface PlannedPaymentRuleDao {
    @Query("SELECT * FROM planned_payment_rules WHERE isDeleted = 0 ORDER BY amount DESC, startDate ASC")
    suspend fun findAll(): List<PlannedPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<PlannedPaymentRuleEntity>

    @Query(
        "SELECT * FROM planned_payment_rules WHERE isDeleted = 0 AND oneTime = :oneTime ORDER BY amount DESC, startDate ASC"
    )
    suspend fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRuleEntity>

    @Query("SELECT * FROM planned_payment_rules WHERE id = :id AND isDeleted = 0")
    suspend fun findById(id: UUID): PlannedPaymentRuleEntity?

    @Query("SELECT COUNT(*) FROM planned_payment_rules WHERE isDeleted = 0 ")
    suspend fun countPlannedPayments(): Long
}
