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
    suspend fun findByIds(id: UUID): TagEntity?

    @Query("SELECT * FROM tags WHERE id in (:ids)")
    suspend fun findByIds(ids: List<UUID>): List<TagEntity>

    @Query("SELECT * FROM tags WHERE name LIKE '%' || :text ||'%'")
    suspend fun findByText(text: String): List<TagEntity>

    @Suppress(
        "AnnotationOnSeparateLine",
        "ArgumentListWrapping",
        "MaximumLineLength",
        "ParameterListWrapping",
        "MaxLineLength"
    )
    @Query(
        "SELECT tags.*,tags_association.associatedId FROM tags LEFT JOIN tags_association ON tags.id = tags_association.tagId " +
                "WHERE associatedId IN (:ids)"
    )
    @RewriteQueriesToDropUnusedColumns
    suspend fun findTagsByAssociatedIds(ids: List<UUID>): Map<@MapColumn(columnName = "associatedId") UUID, List<TagEntity>>

    @Query("SELECT tags.* FROM tags JOIN tags_association ON tags.id = tags_association.tagId WHERE associatedId = :id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun findTagsByAssociatedId(id: UUID): List<TagEntity>
}