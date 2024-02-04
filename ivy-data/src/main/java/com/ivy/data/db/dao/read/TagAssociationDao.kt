package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.TagAssociationEntity
import java.util.UUID

@Dao
interface TagAssociationDao {
    @Query("SELECT * FROM tags_association")
    suspend fun findAll(): List<TagAssociationEntity>

    @Query("SELECT * FROM tags_association WHERE tagId = :id")
    suspend fun findById(id: UUID): TagAssociationEntity?

    @Query("SELECT * FROM tags_association WHERE associatedId = :associatedId")
    suspend fun findByAssociatedId(associatedId: UUID): TagAssociationEntity?
}