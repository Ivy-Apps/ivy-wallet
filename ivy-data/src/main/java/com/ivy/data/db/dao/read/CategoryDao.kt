package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.CategoryEntity
import java.util.*

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun findById(id: UUID): CategoryEntity?

    @Query("SELECT MAX(orderNum) FROM categories")
    suspend fun findMaxOrderNum(): Double?
}
