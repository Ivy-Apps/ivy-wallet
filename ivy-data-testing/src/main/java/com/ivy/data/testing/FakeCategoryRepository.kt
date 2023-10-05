package com.ivy.data.testing

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.CategoryRepository

class FakeCategoryRepository : CategoryRepository {
    private val categories = mutableMapOf<CategoryId, Category>()

    override suspend fun findAll(deleted: Boolean): List<Category> {
        return categories.values
            .filter { !it.removed }
            .sortedBy { it.orderNum }
    }

    override suspend fun findById(id: CategoryId): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun findMaxOrderNum(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun saveMany(values: List<Category>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: CategoryId) {
        TODO("Not yet implemented")
    }

    override suspend fun flagDeleted(id: CategoryId) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}