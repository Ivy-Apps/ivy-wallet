package com.ivy.data.backup.fake

import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import java.util.UUID

class FakeBackupPlannedPaymentDao : PlannedPaymentRuleDao, WritePlannedPaymentRuleDao {
    private val items = mutableListOf<PlannedPaymentRuleEntity>()

    override suspend fun findAll(): List<PlannedPaymentRuleEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<PlannedPaymentRuleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRuleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): PlannedPaymentRuleEntity? {
        return items.find { it.id == id }
    }

    override suspend fun countPlannedPayments(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: PlannedPaymentRuleEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<PlannedPaymentRuleEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun flagDeleted(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun flagDeletedByAccountId(accountId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}