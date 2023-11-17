package com.ivy.data.repository

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId

interface CategoryRepository {
    suspend fun findAll(deleted: Boolean): List<Category>
    suspend fun findById(id: CategoryId): Category?
    suspend fun findMaxOrderNum(): Double
    suspend fun save(value: Category)
    suspend fun saveMany(values: List<Category>)
    suspend fun deleteById(id: CategoryId)
    suspend fun flagDeleted(id: CategoryId)
    suspend fun deleteAll()
}