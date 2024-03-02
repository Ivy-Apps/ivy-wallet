package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.model.Tag
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.TagId
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.mapper.TagMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TagsRepositoryImpl @Inject constructor(
    private val mapper: TagMapper,
    private val tagDao: TagDao,
    private val writeTagDao: WriteTagDao,
    private val writeTagAssociationDao: WriteTagAssociationDao,
    private val dispatchersProvider: DispatchersProvider
) : TagsRepository {
    override suspend fun findById(id: TagId): Tag? {
        return withContext(dispatchersProvider.io) {
            tagDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }
            }
        }
    }

    override suspend fun findByAssociatedId(id: AssociationId): List<Tag> {
        return withContext(dispatchersProvider.io) {
            tagDao.findTagsByAssociatedId(id.value).let {
                with(mapper) { it.map { it.toDomain() } }
            }
        }
    }

    override suspend fun findByAssociatedId(ids: List<AssociationId>): Map<AssociationId, List<Tag>> {
        return withContext(dispatchersProvider.io) {
            tagDao.findTagsByAssociatedIds(ids.map { it.value }).entries.associate { (id, tags) ->
                AssociationId(id) to with(mapper) { tags.map { it.toDomain() } }
            }
        }
    }

    override suspend fun findAll(deleted: Boolean): List<Tag> {
        return withContext(dispatchersProvider.io) {
            tagDao.findAll().let {
                with(mapper) { it.map { it.toDomain() } }
            }
        }
    }

    override suspend fun findByText(text: String): List<Tag> {
        return withContext(dispatchersProvider.io) {
            tagDao.findByText(text).let {
                with(mapper) { it.map { it.toDomain() } }
            }
        }
    }

    override suspend fun associateTagToEntity(associationId: AssociationId, tagId: TagId) {
        withContext(dispatchersProvider.io) {
            writeTagAssociationDao.save(
                with(mapper) {
                    createNewTagAssociation(tagId, associationId).toEntity()
                }
            )
        }
    }

    override suspend fun removeTagAssociation(associationId: AssociationId, tagId: TagId) {
        withContext(dispatchersProvider.io) {
            writeTagAssociationDao.deleteId(tagId = tagId.value, associatedId = associationId.value)
        }
    }

    override suspend fun save(value: Tag) {
        withContext(dispatchersProvider.io) {
            writeTagDao.save(with(mapper) { value.toEntity() })
        }
    }

    override suspend fun updateTag(tagId: TagId, value: Tag) {
        withContext(dispatchersProvider.io) {
            writeTagDao.update(with(mapper) { value.toEntity() })
        }
    }

    override suspend fun deleteById(id: TagId) {
        withContext(dispatchersProvider.io) {
            writeTagAssociationDao.deleteAssociationsByTagId(id.value)
            writeTagDao.deleteById(id.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            writeTagDao.deleteAll()
        }
    }
}
