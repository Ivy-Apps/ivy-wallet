package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class CategoryMapperTest : FreeSpec({

    "maps Category to Entity" {
        // given
        val mapper = CategoryMapper()
        val categoryId = CategoryId(UUID.randomUUID())
        val category = Category(
            name = NotBlankTrimmedString("Home"),
            color = ColorInt(42),
            icon = null,
            orderNum = 0.0,
            removed = false,
            lastUpdated = Instant.EPOCH,
            id = categoryId
        )

        // when
        val res = with(mapper) { category.toEntity() }

        // then
        res shouldBe CategoryEntity(
            name = "Home",
            color = 42,
            icon = null,
            orderNum = 0.0,
            isSynced = true,
            isDeleted = false,
            id = categoryId.value
        )
    }
})