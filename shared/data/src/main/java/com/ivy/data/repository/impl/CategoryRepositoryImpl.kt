package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.mapper.CategoryMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val mapper: CategoryMapper,
    private val writeCategoryDao: WriteCategoryDao,
    private val categoryDao: CategoryDao,
    private val dispatchersProvider: DispatchersProvider,
) : CategoryRepository {
    override suspend fun findAll(deleted: Boolean): List<Category> {
        return withContext(dispatchersProvider.io) {
            categoryDao.findAll(deleted).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findById(id: CategoryId): Category? {
        return withContext(dispatchersProvider.io) {
            categoryDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return withContext(dispatchersProvider.io) {
            categoryDao.findMaxOrderNum() ?: 0.0
        }
    }

    override suspend fun save(value: Category) {
        return withContext(dispatchersProvider.io) {
            writeCategoryDao.save(
                with(mapper) { value.toEntity() }
            )
        }
    }

    override suspend fun saveMany(values: List<Category>) {
        withContext(dispatchersProvider.io) {
            writeCategoryDao.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
        }
    }

    override suspend fun deleteById(id: CategoryId) {
        withContext(dispatchersProvider.io) {
            writeCategoryDao.deleteById(id.value)
        }
    }

    override suspend fun flagDeleted(id: CategoryId) {
        withContext(dispatchersProvider.io) {
            writeCategoryDao.flagDeleted(id.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            writeCategoryDao.deleteAll()
        }
    }
}
