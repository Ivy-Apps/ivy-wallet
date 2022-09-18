package com.ivy.core.persistence.dao.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: Iterable<CategoryEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM categories WHERE sync != $DELETING ORDER BY orderNum ASC")
    fun findAll(): Flow<List<CategoryEntity>>
    // endregion

    // region Update
    @Query("UPDATE categories SET sync = :sync WHERE id IN (:categoryIds)")
    suspend fun updateSync(categoryIds: List<String>, sync: SyncState)
    // endregion
}