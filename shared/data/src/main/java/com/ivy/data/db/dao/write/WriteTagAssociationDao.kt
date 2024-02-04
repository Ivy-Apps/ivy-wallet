package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.data.db.entity.TagAssociationEntity
import java.util.*

@Dao
interface WriteTagAssociationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TagAssociationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TagAssociationEntity>)

    @Query("DELETE FROM tags_association")
    suspend fun deleteAll()

    @Query("DELETE FROM tags_association WHERE tagId = :tagId AND associatedId = :associatedId")
    suspend fun deleteId(tagId: UUID, associatedId: UUID)

    @Query("DELETE FROM tags_association WHERE tagId = :tagId")
    suspend fun deleteAssociationsByTagId(tagId: UUID)

    @Query("DELETE FROM tags_association WHERE associatedId = :associatedId")
    suspend fun deleteAssociationsByAssociateId(associatedId: UUID)
}