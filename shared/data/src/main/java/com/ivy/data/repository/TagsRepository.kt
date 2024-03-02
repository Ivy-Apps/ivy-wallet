package com.ivy.data.repository

import com.ivy.data.model.Tag
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.TagId

interface TagsRepository {
    suspend fun findById(id: TagId): Tag?
    suspend fun findByAssociatedId(id: AssociationId): List<Tag>
    suspend fun findByAssociatedId(ids: List<AssociationId>): Map<AssociationId, List<Tag>>
    suspend fun findAll(deleted: Boolean = false): List<Tag>
    suspend fun findByText(text: String): List<Tag>
    suspend fun associateTagToEntity(associationId: AssociationId, tagId: TagId)
    suspend fun removeTagAssociation(associationId: AssociationId, tagId: TagId)
    suspend fun save(value: Tag)
    suspend fun updateTag(tagId: TagId, value: Tag)
    suspend fun deleteById(id: TagId)
    suspend fun deleteAll()
}
