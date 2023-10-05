package com.ivy.data.testing

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class FakeCategoryRepositoryTest : FreeSpec({
    fun newRepository() = FakeCategoryRepository()

    "find all" - {
        "not deleted" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )

            // when
            repository.saveMany(categories)
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                )
            )
        }

        "deleted" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )

            // when
            repository.saveMany(categories)
            val res = repository.findAll(true)

            // then
            res shouldBe listOf(
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )
        }

        "empty list" {
            // given
            val repository = newRepository()
            val categories = emptyList<Category>()

            // when
            repository.saveMany(categories)
            val res = repository.findAll(false)

            // then
            res shouldBe emptyList()
        }
    }

    "find by id" - {
        "existing id" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val category2 = Category(
                name = NotBlankTrimmedString("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 2.0,
                removed = true,
                lastUpdated = Instant.EPOCH,
                id = id2
            )
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                category2
            )

            // when
            repository.saveMany(categories)
            val res = repository.findById(id2)

            // then
            res shouldBe category2
        }

        "not existing id" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )

            // when
            repository.saveMany(categories)
            val res = repository.findById(CategoryId(UUID.randomUUID()))

            // then
            res shouldBe null
        }
    }

    "find max order num" - {
        "of categories" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )

            // when
            repository.saveMany(categories)
            val res = repository.findMaxOrderNum()

            // then
            res shouldBe 2.0
        }

        "of empty list" - {
            // given
            val repository = newRepository()
            val categories = emptyList<Category>()

            // when
            repository.saveMany(categories)
            val res = repository.findMaxOrderNum()

            // then
            res shouldBe (0.0)
        }
    }

    "save" - {
        "create new" {
            // given
            val repository = newRepository()
            val id = CategoryId(UUID.randomUUID())
            val category = Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id
            )

            // when
            repository.save(category)
            val res = repository.findById(id)

            // then
            res shouldBe category
        }

        "update existing" {
            // given
            val repository = newRepository()
            val id = CategoryId(UUID.randomUUID())
            val category = Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id
            )
            val updated = category.copy(
                name = NotBlankTrimmedString("My Home")
            )

            // when
            repository.save(category)
            repository.save(updated)
            val res = repository.findAll(deleted = false)

            // then
            res shouldBe listOf(updated)
        }
    }

    "save many" {
        // given
        val repository = newRepository()
        val id1 = CategoryId(UUID.randomUUID())
        val id2 = CategoryId(UUID.randomUUID())
        val categories = listOf(
            Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id1
            ),
            Category(
                name = NotBlankTrimmedString("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 2.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id2
            )
        )

        // when
        repository.saveMany(categories)
        val res = repository.findAll(false)

        // then
        res shouldBe categories
    }

    "flag deleted" - {
        "existing id" {
            // given
            val repository = newRepository()
            val id = CategoryId(UUID.randomUUID())
            val category = Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id
            )

            // when
            repository.save(category)
            repository.flagDeleted(id)
            val res = repository.findById(id)

            // then
            res shouldBe category.copy(removed = true)
        }

        "not existing id" {
            // given
            val repository = newRepository()
            val id = CategoryId(UUID.randomUUID())
            val category = Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id
            )

            // when
            repository.save(category)
            repository.flagDeleted(CategoryId(UUID.randomUUID()))
            val res = repository.findById(id)

            // then
            res shouldBe category
        }
    }

    "delete by id" {
        // given
        val repository = newRepository()
        val id1 = CategoryId(UUID.randomUUID())
        val id2 = CategoryId(UUID.randomUUID())
        val categories = listOf(
            Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id1
            ),
            Category(
                name = NotBlankTrimmedString("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 2.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id2
            )
        )

        // when
        repository.saveMany(categories)
        repository.deleteById(id2)
        val res = repository.findAll(false)

        // then
        res shouldBe listOf(
            Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id1
            )
        )
    }

    "delete all" {
        // given
        val repository = newRepository()
        val id1 = CategoryId(UUID.randomUUID())
        val id2 = CategoryId(UUID.randomUUID())
        val categories = listOf(
            Category(
                name = NotBlankTrimmedString("Home"),
                color = ColorInt(42),
                icon = null,
                orderNum = 1.0,
                removed = false,
                lastUpdated = Instant.EPOCH,
                id = id1
            ),
            Category(
                name = NotBlankTrimmedString("Fun"),
                color = ColorInt(42),
                icon = null,
                orderNum = 2.0,
                removed = true,
                lastUpdated = Instant.EPOCH,
                id = id2
            )
        )

        // when
        repository.saveMany(categories)
        repository.deleteAll()
        val notDeleted = repository.findAll(false)
        val deleted = repository.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
})