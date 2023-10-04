package com.ivy.data.repository.impl

import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.mapper.CategoryMapper
import com.ivy.data.source.LocalCategoryDataSource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.Instant
import java.util.UUID

class CategoryRepositoryImplTest : FreeSpec({
    val dataSource = mockk<LocalCategoryDataSource>()

    fun newRepository(): CategoryRepository = CategoryRepositoryImpl(
        mapper = CategoryMapper(),
        dataSource = dataSource
    )

    "find all not deleted" - {
        "empty list" {
            // given
            val repository = newRepository()
            coEvery { dataSource.findAll(false) } returns emptyList()

            // when
            val res = repository.findAll(false)

            // then
            res shouldBe emptyList()
        }

        "valid and invalid categories" {
            // given
            val repository = newRepository()
            val id1 = UUID.randomUUID()
            val id3 = UUID.randomUUID()
            coEvery { dataSource.findAll(false) } returns listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 0.0,
                    isSynced = true,
                    isDeleted = false,
                    id = id1
                ),
                CategoryEntity(
                    name = "",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isSynced = true,
                    isDeleted = false,
                    id = UUID.randomUUID()
                ),
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isSynced = true,
                    isDeleted = false,
                    id = id3
                )
            )

            // when
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 0.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = CategoryId(id1)
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = CategoryId(id3)
                )
            )
        }
    }

    "find by id" - {
        "null CategoryEntity" {
            // given
            val repository = newRepository()
            val id = UUID.randomUUID()
            coEvery { dataSource.findById(id) } returns null

            // when
            val category = repository.findById(CategoryId(id))

            // then
            category shouldBe null
        }

        "valid CategoryEntity" {
            // given
            val repository = newRepository()
            val id = UUID.randomUUID()
            coEvery { dataSource.findById(id) } returns CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 0.0,
                isSynced = true,
                isDeleted = false,
                id = id
            )

            // when
            val category = repository.findById(CategoryId(id))

            // then
            category shouldBe Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 0.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = CategoryId(id)
            )
        }

        "invalid CategoryEntity" {
            // given
            val repository = newRepository()
            val id = UUID.randomUUID()
            coEvery { dataSource.findById(id) } returns CategoryEntity(
                name = "",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isSynced = true,
                isDeleted = false,
                id = UUID.randomUUID()
            )

            // when
            val category = repository.findById(CategoryId(id))

            // then
            category shouldBe null
        }
    }

    "find max order num" - {
        "null from the source" {
            // given
            val repository = newRepository()
            coEvery { dataSource.findMaxOrderNum() } returns null

            // when
            val num = repository.findMaxOrderNum()

            // then
            num shouldBe 0.0
        }

        "number from the source" {
            // given
            val repository = newRepository()
            coEvery { dataSource.findMaxOrderNum() } returns 15.0

            // when
            val num = repository.findMaxOrderNum()

            // then
            num shouldBe 15.0
        }
    }
})