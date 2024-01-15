package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.data.db.entity.TagTransactionEntity
import java.util.*

@Dao
interface WriteTagTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TagTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TagTransactionEntity>)

    @Query("DELETE FROM tags_transaction")
    suspend fun deleteAll()

    @Query("DELETE FROM tags_transaction WHERE tagId = :tagId AND associatedId = :associatedId")
    suspend fun deleteId(tagId: UUID, associatedId: UUID)

    @Query("DELETE FROM tags_transaction WHERE tagId = :tagId")
    suspend fun deleteAssociationsByTagId(tagId: UUID)

    @Query("DELETE FROM tags_transaction WHERE associatedId = :associatedId")
    suspend fun deleteAssociationsByAssociateId(associatedId: UUID)
}