package com.ivy.data.backup.fake

import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.db.entity.LoanEntity
import java.util.UUID

class FakeBackupLoanDao : LoanDao, WriteLoanDao {
    private val items = mutableListOf<LoanEntity>()

    override suspend fun findAll(): List<LoanEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<LoanEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): LoanEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: LoanEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<LoanEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun flagDeleted(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}