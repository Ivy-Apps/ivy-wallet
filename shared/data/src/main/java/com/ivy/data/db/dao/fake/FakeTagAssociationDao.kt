package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.entity.TagAssociationEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeTagAssociationDao : TagAssociationDao, WriteTagAssociationDao {
    private val items = mutableListOf<TagAssociationEntity>()
    override suspend fun findAll(): List<TagAssociationEntity> {
        return items
    }

    override suspend fun findById(id: UUID): TagAssociationEntity? {
        return items.find { it.tagId == id }
    }

    override suspend fun findByAssociatedId(associatedId: UUID): TagAssociationEntity? {
        return items.find { it.associatedId == associatedId }
    }

    override suspend fun findByAllAssociatedIdForTagId(tagIds: List<UUID>): Map<UUID, List<TagAssociationEntity>> {
        val setOfIds = tagIds.toSet()
        return items.filter { setOfIds.contains(it.tagId) }.groupBy(
            { it.tagId },
            { it }
        )
    }

    override suspend fun save(value: TagAssociationEntity) {
        items.add(value)
    }

    override suspend fun save(value: List<TagAssociationEntity>) {
        items.addAll(value)
    }

    override suspend fun deleteAll() {
        items.clear()
    }

    override suspend fun deleteId(tagId: UUID, associatedId: UUID) {
        items.removeIf { it.tagId == tagId && it.associatedId == associatedId }
    }

    override suspend fun deleteAssociationsByTagId(tagId: UUID) {
        items.removeIf { it.tagId == tagId }
    }

    override suspend fun deleteAssociationsByAssociateId(associatedId: UUID) {
        items.removeIf { it.associatedId == associatedId }
    }
}