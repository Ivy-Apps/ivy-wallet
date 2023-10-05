package com.ivy.data.repository.impl

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.mapper.CategoryMapper
import com.ivy.data.source.LocalCategoryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val mapper: CategoryMapper,
    private val dataSource: LocalCategoryDataSource
) : CategoryRepository {
    override suspend fun findAll(deleted: Boolean): List<Category> {
        return withContext(Dispatchers.IO) {
            dataSource.findAll(deleted).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findById(id: CategoryId): Category? {
        return withContext(Dispatchers.IO) {
            dataSource.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return withContext(Dispatchers.IO) {
            dataSource.findMaxOrderNum() ?: 0.0
        }
    }

    override suspend fun save(value: Category) {
        return withContext(Dispatchers.IO) {
            dataSource.save(
                with(mapper) { value.toEntity() }
            )
        }
    }

    override suspend fun saveMany(values: List<Category>) {
        withContext(Dispatchers.IO) {
            dataSource.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
        }
    }

    override suspend fun deleteById(id: CategoryId) {
        withContext(Dispatchers.IO) {
            dataSource.deleteById(id.value)
        }
    }

    override suspend fun flagDeleted(id: CategoryId) {
        withContext(Dispatchers.IO) {
            dataSource.flagDeleted(id.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAll()
        }
    }
}