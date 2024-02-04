package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.TagEntity
import com.ivy.data.model.Tag
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.TagId
import javax.inject.Inject

class TagMapper @Inject constructor() {

    fun TagEntity.toDomain(): Tag {
        return Tag(
            id = TagId(this.id),
            name = NotBlankTrimmedString(this.name),
            description = this.description,
            color = ColorInt(this.color),
            icon = this.icon?.let { IconAsset(it) },
            orderNum = this.orderNum,
            creationTimestamp = this.dateTime,
            lastUpdated = this.lastSyncedTime,
            removed = this.isDeleted
        )
    }

    fun Tag.toEntity(): TagEntity {
        return TagEntity(
            id = this.id.value,
            name = this.name.value,
            description = this.description,
            color = this.color.value,
            icon = this.icon?.id,
            orderNum = this.orderNum,
            dateTime = this.creationTimestamp,
            lastSyncedTime = this.lastUpdated,
            isDeleted = this.removed
        )
    }
}