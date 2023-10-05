package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
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
            orderNum = 1.0,
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
            orderNum = 1.0,
            isSynced = true,
            isDeleted = false,
            id = categoryId.value
        )
    }

    "maps CategoryEntity to domain" - {
        // given
        val categoryId = CategoryId(UUID.randomUUID())
        val mapper = CategoryMapper()
        val categoryEntity = CategoryEntity(
            name = "Home",
            color = 42,
            icon = null,
            orderNum = 1.0,
            isSynced = true,
            isDeleted = false,
            id = categoryId.value
        )

        "valid entity" {
            // when
            val res = with(mapper) { categoryEntity.toDomain() }

            // then
            res.shouldBeRight() shouldBe Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = categoryId
            )
        }

        "name missing" {
            // given
            val corruptedEntity = categoryEntity.copy(name = "")

            // when
            val res = with(mapper) { corruptedEntity.toDomain() }

            // then
            res.shouldBeLeft()
        }

        "missing icon is okay" {
            // given
            val missingIconEntity = categoryEntity.copy(icon = null)

            // when
            val res = with(mapper) { missingIconEntity.toDomain() }

            // then
            res.shouldBeRight()
        }
    }
})