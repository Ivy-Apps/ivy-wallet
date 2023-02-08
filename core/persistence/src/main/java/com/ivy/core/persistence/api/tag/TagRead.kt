package com.ivy.core.persistence.api.tag

import com.ivy.core.data.Tag
import com.ivy.core.data.TagId
import com.ivy.core.persistence.api.ReadSyncable

interface TagRead : ReadSyncable<Tag, TagId, TagQuery> {
}

sealed interface TagQuery
