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

        }
    }

    override suspend fun findMaxOrderNum(): Double? {
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