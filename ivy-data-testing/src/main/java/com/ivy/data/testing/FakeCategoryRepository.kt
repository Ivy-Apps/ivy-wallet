package com.ivy.data.testing

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.CategoryRepository

class FakeCategoryRepository : CategoryRepository {
    private val categories = mutableListOf<Category>()

    override suspend fun findAll(deleted: Boolean): List<Category> {
        return categories
            .filter { it.removed == deleted }
            .sortedBy { it.orderNum }
    }

    override suspend fun findById(id: CategoryId): Category? {
        return categories.firstOrNull { it.id == id }
    }

    override suspend fun findMaxOrderNum(): Double {
        return categories.maxOfOrNull { it.orderNum } ?: 0.0
    }

    override suspend fun save(value: Category) {
        if (findById(value.id) == null) {
            categories.add(value)
        } else {
            deleteById(value.id)
            categories.add(value)
        }
    }

    override suspend fun saveMany(values: List<Category>) {
        values.forEach {
            save(it)
        }
    }

    override suspend fun deleteById(id: CategoryId) {
        categories.removeIf { it.id == id }
    }

    override suspend fun flagDeleted(id: CategoryId) {
        val category = categories.find { it.id == id } ?: return
        categories.remove(category)
        categories.add(category.copy(removed = true))
    }

    override suspend fun deleteAll() {
        categories.clear()
    }
}