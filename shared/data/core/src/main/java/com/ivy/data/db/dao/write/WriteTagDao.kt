package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.TagEntity
import java.util.UUID

@Dao
interface WriteTagDao {
    @Upsert
    suspend fun save(value: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TagEntity>)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM tags")
    suspend fun deleteAll()
}