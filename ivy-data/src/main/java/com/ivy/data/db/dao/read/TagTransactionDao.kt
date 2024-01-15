package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.TagTransactionEntity
import java.util.UUID

@Dao
interface TagTransactionDao {
    @Query("SELECT * FROM tags_transaction")
    suspend fun findAll(): List<TagTransactionEntity>

    @Query("SELECT * FROM tags_transaction WHERE tagId = :id")
    suspend fun findById(id: UUID): TagTransactionEntity?

    @Query("SELECT * FROM tags_transaction WHERE associatedId = :associatedId")
    suspend fun findByAssociatedId(associatedId: UUID): TagTransactionEntity?
}