package com.ivy.data.repository

import com.ivy.data.model.Tag
import com.ivy.data.model.TagAssociation
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.TagId

interface TagsRepository {
    suspend fun findByIds(id: TagId): com.ivy.data.model.Tag?
    suspend fun findByIds(ids: List<TagId>): List<com.ivy.data.model.Tag>
    suspend fun findByAssociatedId(id: AssociationId): List<com.ivy.data.model.Tag>
    suspend fun findByAssociatedId(ids: List<AssociationId>): Map<AssociationId, List<com.ivy.data.model.Tag>>
    suspend fun findAll(deleted: Boolean = false): List<com.ivy.data.model.Tag>
    suspend fun findByText(text: String): List<com.ivy.data.model.Tag>
    suspend fun findByAllAssociatedIdForTagId(tagIds: List<TagId>): Map<TagId, List<com.ivy.data.model.TagAssociation>>
    suspend fun findByAllTagsForAssociations(): Map<AssociationId, List<com.ivy.data.model.TagAssociation>>
    suspend fun associateTagToEntity(associationId: AssociationId, tagId: TagId)
    suspend fun removeTagAssociation(associationId: AssociationId, tagId: TagId)
    suspend fun save(value: com.ivy.data.model.Tag)
    suspend fun updateTag(tagId: TagId, value: com.ivy.data.model.Tag)
    suspend fun deleteById(id: TagId)
    suspend fun deleteAll()
}
