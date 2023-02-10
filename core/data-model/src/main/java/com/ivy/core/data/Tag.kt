package com.ivy.core.data

import com.ivy.core.data.common.Archiveable
import com.ivy.core.data.common.IvyColor
import com.ivy.core.data.common.Reorderable
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId
import java.time.LocalDateTime
import java.util.*

data class Tag(
    override val id: TagId,
    val name: String,
    val description: String?,
    val color: IvyColor,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class TagId(override val uuid: UUID) : UniqueId