package com.ivy.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import java.time.Instant
import javax.inject.Inject

class CategoryMapper @Inject constructor() {
    val test = "Hello"

    fun CategoryEntity.toDomain(): Either<String, Category> = either {
        Category(
            id = CategoryId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            color = ColorInt(color),
            icon = icon?.let { IconAsset.from(it).getOrNull() },
            orderNum = orderNum,
            lastUpdated = Instant.EPOCH,
            removed = isDeleted
        )
    }

    fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            name = name.value,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            isSynced = true,
            isDeleted = removed,
            id = id.value
        )
    }
}