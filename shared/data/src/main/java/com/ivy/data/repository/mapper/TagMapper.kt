package com.ivy.data.repository.mapper

import android.graphics.Color
import arrow.core.Either
import arrow.core.raise.either
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

    fun TagEntity.toDomain(): Either<String, Tag> = either {
        Tag(
            id = TagId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            description = description,
            color = ColorInt(color),
            icon = icon?.let(IconAsset::from)?.getOrNull(),
            orderNum = orderNum,
            creationTimestamp = dateTime,
            lastUpdated = lastSyncedTime,
            removed = isDeleted
        )
    }

    fun Tag.toEntity(): TagEntity {
        return TagEntity(
            id = id.value,
            name = name.value,
            description = description,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            dateTime = creationTimestamp,
            lastSyncedTime = lastUpdated,
            isDeleted = removed
        )
    }

    fun TagAssociation.toEntity(): TagAssociationEntity {
        return TagAssociationEntity(
            tagId = id.value,
            associatedId = associatedId.value,
            lastSyncedTime = lastUpdated,
            isDeleted = removed
        )
    }

    fun TagAssociationEntity.toDomain(): TagAssociation {
        return TagAssociation(
            id = TagId(tagId),
            associatedId = AssociationId(associatedId),
            lastUpdated = lastSyncedTime,
            removed = isDeleted
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