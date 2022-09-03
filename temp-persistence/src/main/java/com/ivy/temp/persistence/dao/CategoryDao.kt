package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.CategoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAllSuspend(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY orderNum ASC")
    fun findAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun findById(id: UUID): CategoryEntity?

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE categories SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("SELECT MAX(orderNum) FROM categories")
    suspend fun findMaxOrderNum(): Double?

    @Query("SELECT * FROM categories WHERE isDeleted = 0 AND parentCategoryId IS NULL ORDER BY orderNum ASC")
    suspend fun findAllParentCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isDeleted = 0 AND parentCategoryId = :id ORDER BY orderNum ASC")
    suspend fun findAllSubCategories(id:UUID): List<CategoryEntity>
}