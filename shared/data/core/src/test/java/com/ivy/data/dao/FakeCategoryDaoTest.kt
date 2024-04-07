package com.ivy.data.dao

import com.ivy.data.db.dao.fake.FakeCategoryDao
import com.ivy.data.db.entity.CategoryEntity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class FakeCategoryDaoTest {

    private lateinit var dao: FakeCategoryDao

    @Before
    fun setup() {
        dao = FakeCategoryDao()
    }

    @Test
    fun `find all - not deleted`() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findAll(false)

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

    @Test
    fun deleted() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findAll(true)

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

    @Test
    fun `empty list`() = runTest {
        // given
        val categories = emptyList<CategoryEntity>()

        // when
        dao.saveMany(categories)
        val res = dao.findAll(false)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find by id - existing id`() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findById(id2)

        // then
        res shouldBe category2
    }

    @Test
    fun `find by id - not existing id`() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findById(UUID.randomUUID())

        // then
        res shouldBe null
    }

    @Test
    fun `find max order num of categories`() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findMaxOrderNum()

        // then
        res shouldBe 2.0
    }

    @Test
    fun `find max order num of empty list`() = runTest {
        // given
        val categories = emptyList<CategoryEntity>()

        // when
        dao.saveMany(categories)
        val res = dao.findMaxOrderNum()

        // then
        res shouldBe null
    }

    @Test
    fun `save - create new`() = runTest {
        // given
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
        dao.save(category)
        val res = dao.findById(id)

        // then
        res shouldBe category
    }

    @Test
    fun `save - update existing`() = runTest {
        // given
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
        dao.save(category)
        dao.save(updated)
        val res = dao.findAll(deleted = false)

        // then
        res shouldBe listOf(updated)
    }

    @Test
    fun `save many`() = runTest {
        // given
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
        dao.saveMany(categories)
        val res = dao.findAll(false)

        // then
        res shouldBe categories
    }

    @Test
    fun `flag deleted - existing id`() = runTest {
        // given
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
        dao.save(category)
        dao.flagDeleted(id)
        val res = dao.findById(id)

        // then
        res shouldBe category.copy(isDeleted = true)
    }

    @Test
    fun `flag deleted - not existing id`() = runTest {
        // given
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
        dao.save(category)
        dao.flagDeleted(UUID.randomUUID())
        val res = dao.findById(id)

        // then
        res shouldBe category
    }

    @Test
    fun `delete by id`() = runTest {
        // given
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
        dao.saveMany(categories)
        dao.deleteById(id2)
        val res = dao.findAll(false)

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

    @Test
    fun `delete all`() = runTest {
        // given
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
        dao.saveMany(categories)
        dao.deleteAll()
        val notDeleted = dao.findAll(false)
        val deleted = dao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
}
