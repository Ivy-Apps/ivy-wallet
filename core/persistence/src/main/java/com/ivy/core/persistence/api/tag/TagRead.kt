package com.ivy.core.persistence.api.tag

import com.ivy.core.data.Tag
import com.ivy.core.data.TagId
import com.ivy.core.persistence.api.Read

interface TagRead : Read<Tag, TagId, TagRead.Query> {
    sealed interface Query
}