package com.ivy.data.source

import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.entity.CategoryEntity
import java.util.UUID
import javax.inject.Inject

class LocalCategoryDataSource @Inject constructor(
    private val categoryDao: CategoryDao,
    private val writeCategoryDao: WriteCategoryDao,
) {
    suspend fun findAll(deleted: Boolean): List<CategoryEntity> {
        return categoryDao.findAll(deleted)
    }

    suspend fun findById(id: UUID): CategoryEntity? {
        return categoryDao.findById(id)
    }

    suspend fun findMaxOrderNum(): Double? {
        return categoryDao.findMaxOrderNum()
    }

    suspend fun save(value: CategoryEntity) {
        writeCategoryDao.save(value)
    }

    suspend fun saveMany(values: List<CategoryEntity>) {
        writeCategoryDao.saveMany(values)
    }

    suspend fun deleteById(id: UUID) {
        writeCategoryDao.deleteById(id)
    }

    suspend fun flagDeleted(id: UUID) {
        writeCategoryDao.flagDeleted(id)
    }

    suspend fun deleteAll() {
        writeCategoryDao.deleteAll()
    }
}