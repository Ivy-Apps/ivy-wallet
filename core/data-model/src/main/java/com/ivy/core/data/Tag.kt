package com.ivy.core.data

import com.ivy.core.data.common.Archiveable
import com.ivy.core.data.common.IvyColor
import com.ivy.core.data.common.Reorderable
import com.ivy.core.data.common.Syncable
import java.time.LocalDateTime
import java.util.*

data class Tag(
    val id: UUID,
    val name: String,
    val description: String?,
    val color: IvyColor,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class TagId(val id: UUID)