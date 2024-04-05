package com.ivy.data.repository

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId

interface CategoryRepository {
    suspend fun findAll(deleted: Boolean = false): List<com.ivy.data.model.Category>
    suspend fun findById(id: com.ivy.data.model.CategoryId): com.ivy.data.model.Category?
    suspend fun findMaxOrderNum(): Double

    suspend fun save(value: com.ivy.data.model.Category)
    suspend fun saveMany(values: List<com.ivy.data.model.Category>)
    suspend fun deleteById(id: com.ivy.data.model.CategoryId)
    suspend fun deleteAll()
}
