package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.entity.CategoryEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeCategoryDao : CategoryDao, WriteCategoryDao {
    private val items = mutableListOf<CategoryEntity>()

    override suspend fun findAll(deleted: Boolean): List<CategoryEntity> {
        return items
    }

    override suspend fun findById(id: UUID): CategoryEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double? {
        return items.maxOfOrNull { it.orderNum }
    }

    override suspend fun save(value: CategoryEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<CategoryEntity>) {
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