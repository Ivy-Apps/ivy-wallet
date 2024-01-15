package com.ivy.legacy.datamodel

import com.ivy.data.db.entity.TagEntity
import java.time.LocalDateTime
import java.util.UUID

data class Tag(
    val id: UUID = UUID.randomUUID(),

    val name: String,
    val description: String? = null,

    val color: Int,
    val icon: String? = null,
    val orderNum: Double = 0.0,

    val dateTime: LocalDateTime? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val featureBit: Int = 0,
    val featureBitValue: String? = null
) {
    fun toEntity(): TagEntity {
        return TagEntity(
            id,
            name,
            description,
            color,
            icon,
            orderNum,
            isSynced,
            isDeleted,
            dateTime,
            featureBit,
            featureBitValue
        )
    }
}