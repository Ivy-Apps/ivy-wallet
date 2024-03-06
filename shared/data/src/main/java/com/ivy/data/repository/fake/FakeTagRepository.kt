package com.ivy.data.repository.fake

import com.ivy.base.TestDispatchersProvider
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.impl.TagsRepositoryImpl
import com.ivy.data.repository.mapper.TagMapper
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeTagRepository(
    tagDao: TagDao,
    tagAssociationDao: TagAssociationDao,
    writeTagDao: WriteTagDao,
    writeTagAssociationDao: WriteTagAssociationDao,
    private val tagsRepository: TagsRepository = TagsRepositoryImpl(
        mapper = TagMapper(),
        tagDao = tagDao,
        tagAssociationDao = tagAssociationDao,
        writeTagDao = writeTagDao,
        writeTagAssociationDao = writeTagAssociationDao,
        dispatchersProvider = TestDispatchersProvider

    )
) : TagsRepository by tagsRepository