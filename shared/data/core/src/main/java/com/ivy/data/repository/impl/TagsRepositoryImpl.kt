package com.ivy.data.repository.impl

import com.ivy.base.di.AppCoroutineScope
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.DeleteOperation
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.model.Tag
import com.ivy.data.model.TagAssociation
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.TagId
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.mapper.TagMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsRepositoryImpl @Inject constructor(
    private val mapper: TagMapper,
    private val tagDao: TagDao,
    private val tagAssociationDao: TagAssociationDao,
    private val writeTagDao: WriteTagDao,
    private val writeTagAssociationDao: WriteTagAssociationDao,
    private val dispatchersProvider: DispatchersProvider,
    private val dataObserver: DataObserver,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope
) : TagsRepository {

    init {
        appCoroutineScope.launch {
            dataObserver.writeEvents.collectLatest { event ->
                when (event) {
                    DataWriteEvent.AllDataChange -> {
                        findAllMemoized = false
                        tagsMemo.clear()
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private val tagsMemo = mutableMapOf<TagId, Tag>()
    private var findAllMemoized: Boolean = false

    override suspend fun findByIds(id: TagId): Tag? {
        return tagsMemo[id] ?: withContext(dispatchersProvider.io) {
            tagDao.findByIds(id.value)?.let {
                with(mapper) { it.toDomain().getOrNull() } ?: return@withContext null
            }.also(::memoize)
        }
    }

    override suspend fun findByIds(ids: List<TagId>): List<Tag> {
        return ids.mapNotNull { tagsMemo[it] ?: findByIds(it) }
    }

    override suspend fun findByAssociatedId(id: AssociationId): List<Tag> {
        return withContext(dispatchersProvider.io) {
            tagDao.findTagsByAssociatedId(id.value).let { entities ->
                entities.mapNotNull {
                    with(mapper) {
                        it.toDomain().getOrNull()
                    }
                }
            }
        }
    }

    override suspend fun findByAssociatedId(
        ids: List<AssociationId>
    ): Map<AssociationId, List<Tag>> {
        return ids.chunked(MAX_SQL_LITE_QUERY_SIZE).map {
            withContext(dispatchersProvider.io) {
                async {
                    tagDao.findTagsByAssociatedIds(it.map { it.value })
                        .entries.associate { (id, tags) ->
                            val domainTags = tags.mapNotNull {
                                with(mapper) {
                                    it.toDomain().getOrNull()
                                }
                            }
                            AssociationId(id) to domainTags
                        }
                }
            }
        }.awaitAll().asSequence()
            .flatMap { it.asSequence() }
            .associate { it.key to it.value }
    }

    override suspend fun findAll(deleted: Boolean): List<Tag> {
        return if (findAllMemoized) {
            tagsMemo.values.sortedByDescending { it.creationTimestamp.epochSecond }
        } else {
            withContext(dispatchersProvider.io) {
                tagDao.findAll().let { entities ->
                    entities.mapNotNull {
                        with(mapper) { it.toDomain().getOrNull() }
                    }
                }
            }.also(::memoize).also {
                findAllMemoized = true
            }
        }
    }

    override suspend fun findByText(text: String): List<Tag> {
        return withContext(dispatchersProvider.io) {
            tagDao.findByText(text).let { entities ->
                entities.mapNotNull {
                    with(mapper) { it.toDomain().getOrNull() }
                }
            }
        }
    }

    override suspend fun findByAllAssociatedIdForTagId(
        tagIds: List<TagId>
    ): Map<TagId, List<TagAssociation>> {
        return withContext(dispatchersProvider.io) {
            tagAssociationDao.findByAllAssociatedIdForTagId(
                tagIds.toRawValues()
            ).entries.associate { (id, associations) ->
                with(mapper) {
                    TagId(id) to associations.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun findByAllTagsForAssociations(): Map<AssociationId, List<TagAssociation>> {
        return withContext(dispatchersProvider.io) {
            tagAssociationDao.findAll().groupBy {
                AssociationId(it.associatedId)
            }.mapValues {
                with(mapper) {
                    it.value.map { it.toDomain() }
                }
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
        }.also {
            memoize(value)
            dataObserver.post(DataWriteEvent.SaveTags(listOf(value)))
        }
    }

    override suspend fun updateTag(tagId: TagId, value: Tag) {
        withContext(dispatchersProvider.io) {
            writeTagDao.update(with(mapper) { value.toEntity() })
            memoize(value)
            dataObserver.post(DataWriteEvent.SaveTags(listOf(value)))
        }
    }

    override suspend fun deleteById(id: TagId) {
        withContext(dispatchersProvider.io) {
            writeTagAssociationDao.deleteAssociationsByTagId(id.value)
            writeTagDao.deleteById(id.value)
            tagsMemo.remove(id)
            dataObserver.post(DataWriteEvent.DeleteTags(DeleteOperation.Just(listOf(id))))
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            tagsMemo.clear()
            writeTagAssociationDao.deleteAll()
            writeTagDao.deleteAll()
            dataObserver.post(DataWriteEvent.DeleteTags(DeleteOperation.All))
        }
    }

    private fun memoize(tag: Tag?) {
        tag?.let { tagsMemo[it.id] = it }
    }

    private fun memoize(tags: List<Tag>) {
        tags.forEach(::memoize)
    }

    private fun List<TagId>.toRawValues(): List<UUID> = this.map { it.value }

    companion object {
        private const val MAX_SQL_LITE_QUERY_SIZE = 999
    }
}
