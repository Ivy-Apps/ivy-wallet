package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.db.entity.BudgetEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeBudgetDao : BudgetDao, WriteBudgetDao {
    private val items = mutableListOf<BudgetEntity>()

    override suspend fun findAll(): List<BudgetEntity> {
        return items
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<BudgetEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): BudgetEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderId }
    }

    override suspend fun save(value: BudgetEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<BudgetEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}