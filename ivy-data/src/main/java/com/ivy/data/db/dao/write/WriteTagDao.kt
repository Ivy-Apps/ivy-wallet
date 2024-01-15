package com.ivy.data.db.dao.write

import androidx.room.*
import com.ivy.data.db.entity.TagEntity
import java.util.*

@Dao
interface WriteTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TagEntity>)

    @Update
    suspend fun update(value: TagEntity)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM tags")
    suspend fun deleteAll()
}