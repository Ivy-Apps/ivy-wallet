package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.ivy.data.db.entity.TagEntity
import java.util.*

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY orderNum ASC")
    suspend fun findAll(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun findById(id: UUID): TagEntity?

    @Query("SELECT * FROM tags WHERE name LIKE '%' || :text ||'%'")
    suspend fun findByText(text: String): List<TagEntity>

    @Suppress("AnnotationOnSeparateLine")
    @Query(
        "SELECT tags.* FROM tags LEFT JOIN tags_association ON tags.id = tags_association.tagId " +
                "WHERE associatedId IN (:ids) GROUP BY tags.id"
    )
    @RewriteQueriesToDropUnusedColumns
    suspend fun findTagsByAssociatedIds(ids: List<UUID>): Map<@MapColumn(columnName = "id") UUID, List<TagEntity>>

    @Query("SELECT tags.* FROM tags JOIN tags_association ON tags.id = tags_association.tagId WHERE associatedId = :id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun findTagsByAssociatedId(id: UUID): List<TagEntity>
}