package com.ivy.core.persistence.dao.tag

import androidx.room.Dao
import androidx.room.Query
import com.ivy.core.persistence.entity.tag.TagEntity
import com.ivy.data.DELETING
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    // region Select
    @Query("SELECT * FROM tags WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id IN (:tagIds) AND sync != $DELETING")
    fun findByTagIds(tagIds: List<String>): Flow<List<TagEntity>>
    // endregion

}