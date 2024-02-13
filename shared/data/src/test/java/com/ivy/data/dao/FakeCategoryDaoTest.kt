package com.ivy.data.dao

import com.ivy.data.db.dao.fake.FakeCategoryDao
import com.ivy.data.db.entity.CategoryEntity
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class FakeCategoryDaoTest : FreeSpec({
    fun newCategoryDao() = FakeCategoryDao()

    "find all" - {
        "not deleted" {
            // given
            val categoryDao = newCategoryDao()
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val categories = listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                ),
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isDeleted = true,
                    id = id2
                )
            )

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findAll(false)

            // then
            res shouldBe listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                )
            )
        }

        "deleted" {
            // given
            val categoryDao = newCategoryDao()
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val categories = listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                ),
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isDeleted = true,
                    id = id2
                )
            )

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findAll(true)

            // then
            res shouldBe listOf(
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isDeleted = true,
                    id = id2
                )
            )
        }

        "empty list" {
            // given
            val categoryDao = newCategoryDao()
            val categories = emptyList<CategoryEntity>()

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findAll(false)

            // then
            res shouldBe emptyList()
        }
    }

    "find by id" - {
        "existing id" {
            // given
            val categoryDao = newCategoryDao()
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val category2 = CategoryEntity(
                name = "Fun",
                color = 42,
                icon = null,
                orderNum = 2.0,
                isDeleted = true,
                id = id2
            )
            val categories = listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                ),
                category2
            )

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findById(id2)

            // then
            res shouldBe category2
        }

        "not existing id" {
            // given
            val categoryDao = newCategoryDao()
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val categories = listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                ),
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isDeleted = true,
                    id = id2
                )
            )

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findById(UUID.randomUUID())

            // then
            res shouldBe null
        }
    }

    "find max order num" - {
        "of categories" {
            // given
            val categoryDao = newCategoryDao()
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val categories = listOf(
                CategoryEntity(
                    name = "Home",
                    color = 42,
                    icon = null,
                    orderNum = 1.0,
                    isDeleted = false,
                    id = id1
                ),
                CategoryEntity(
                    name = "Fun",
                    color = 42,
                    icon = null,
                    orderNum = 2.0,
                    isDeleted = true,
                    id = id2
                )
            )

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findMaxOrderNum()

            // then
            res shouldBe 2.0
        }

        "of empty list" - {
            // given
            val categoryDao = newCategoryDao()
            val categories = emptyList<CategoryEntity>()

            // when
            categoryDao.saveMany(categories)
            val res = categoryDao.findMaxOrderNum()

            // then
            res shouldBe null
        }
    }

    "save" - {
        "create new" {
            // given
            val categoryDao = newCategoryDao()
            val id = UUID.randomUUID()
            val category = CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id
            )

            // when
            categoryDao.save(category)
            val res = categoryDao.findById(id)

            // then
            res shouldBe category
        }

        "update existing" {
            // given
            val categoryDao = newCategoryDao()
            val id = UUID.randomUUID()
            val category = CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id
            )
            val updated = category.copy(name = "My Home")

            // when
            categoryDao.save(category)
            categoryDao.save(updated)
            val res = categoryDao.findAll(deleted = false)

            // then
            res shouldBe listOf(updated)
        }
    }

    "save many" {
        // given
        val categoryDao = newCategoryDao()
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val categories = listOf(
            CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id1
            ),
            CategoryEntity(
                name = "Fun",
                color = 42,
                icon = null,
                orderNum = 2.0,
                isDeleted = false,
                id = id2
            )
        )

        // when
        categoryDao.saveMany(categories)
        val res = categoryDao.findAll(false)

        // then
        res shouldBe categories
    }

    "flag deleted" - {
        "existing id" {
            // given
            val categoryDao = newCategoryDao()
            val id = UUID.randomUUID()
            val category = CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id
            )

            // when
            categoryDao.save(category)
            categoryDao.flagDeleted(id)
            val res = categoryDao.findById(id)

            // then
            res shouldBe category.copy(isDeleted = true)
        }

        "not existing id" {
            // given
            val categoryDao = newCategoryDao()
            val id = UUID.randomUUID()
            val category = CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id
            )

            // when
            categoryDao.save(category)
            categoryDao.flagDeleted(UUID.randomUUID())
            val res = categoryDao.findById(id)

            // then
            res shouldBe category
        }
    }

    "delete by id" {
        // given
        val categoryDao = newCategoryDao()
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val categories = listOf(
            CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id1
            ),
            CategoryEntity(
                name = "Fun",
                color = 42,
                icon = null,
                orderNum = 2.0,
                isDeleted = false,
                id = id2
            )
        )

        // when
        categoryDao.saveMany(categories)
        categoryDao.deleteById(id2)
        val res = categoryDao.findAll(false)

        // then
        res shouldBe listOf(
            CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id1
            )
        )
    }

    "delete all" {
        // given
        val categoryDao = newCategoryDao()
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val categories = listOf(
            CategoryEntity(
                name = "Home",
                color = 42,
                icon = null,
                orderNum = 1.0,
                isDeleted = false,
                id = id1
            ),
            CategoryEntity(
                name = "Fun",
                color = 42,
                icon = null,
                orderNum = 2.0,
                isDeleted = true,
                id = id2
            )
        )

        // when
        categoryDao.saveMany(categories)
        categoryDao.deleteAll()
        val notDeleted = categoryDao.findAll(false)
        val deleted = categoryDao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
})
