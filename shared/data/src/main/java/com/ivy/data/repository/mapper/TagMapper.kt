package com.ivy.data.repository.mapper

import android.graphics.Color
import com.ivy.data.db.entity.TagAssociationEntity
import com.ivy.data.db.entity.TagEntity
import com.ivy.data.model.Tag
import com.ivy.data.model.TagAssociation
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.TagId
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class TagMapper @Inject constructor() {

    companion object {
        fun createNewTagId(): TagId = TagId(UUID.randomUUID())
    }

    fun TagEntity.toDomain(): Tag {
        return Tag(
            id = TagId(this.id),
            name = NotBlankTrimmedString(this.name),
            description = this.description,
            color = ColorInt(this.color),
            icon = this.icon?.let { IconAsset.from(it).getOrNull() },
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

    fun TagAssociation.toEntity(): TagAssociationEntity {
        return TagAssociationEntity(
            tagId = this.id.value,
            associatedId = this.associatedId.value,
            lastSyncedTime = this.lastUpdated,
            isDeleted = this.removed
        )
    }

    fun createNewTag(tagId: TagId = createNewTagId(), name: NotBlankTrimmedString): Tag {
        return Tag(
            id = tagId,
            name = name,
            description = null,
            color = ColorInt(Color.TRANSPARENT),
            icon = null,
            orderNum = 0.0,
            creationTimestamp = Instant.now(),
            lastUpdated = Instant.EPOCH,
            removed = false
        )
    }

    fun createNewTagAssociation(tagId: TagId, associationId: AssociationId): TagAssociation {
        return TagAssociation(
            id = tagId,
            associatedId = associationId,
            lastUpdated = Instant.EPOCH,
            removed = false
        )
    }
}