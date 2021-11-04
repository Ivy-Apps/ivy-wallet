package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.Category
import java.util.*

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: Category)

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY orderNum ASC")
    fun findAll(): List<Category>

    @Query("SELECT * FROM categories WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun findById(id: UUID): Category?

    @Query("SELECT * FROM categories WHERE seCategoryName = :seCategoryName")
    fun findBySeCategoryName(seCategoryName: String): Category?

    @Query("DELETE FROM categories WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE categories SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM categories")
    fun deleteAll()

    @Query("SELECT MAX(orderNum) FROM categories")
    fun findMaxOrderNum(): Double
}