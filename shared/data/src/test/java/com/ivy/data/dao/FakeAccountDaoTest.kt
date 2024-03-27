package com.ivy.data.dao

import com.ivy.data.db.dao.fake.FakeAccountDao
import com.ivy.data.db.entity.AccountEntity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class FakeAccountDaoTest {
    private lateinit var dao: FakeAccountDao

    @Before
    fun setup() {
        dao = FakeAccountDao()
    }

    @Test
    fun `find by id - existing id`() = runTest {
        // given
        val id = UUID.randomUUID()
        val account = AccountEntity(
            id = id,
            name = "Bank",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 1.0,
            isSynced = true
        )

        // when
        dao.save(account)
        val res = dao.findById(id)

        // then
        res shouldBe account
    }

    @Test
    fun `find by id - not existing id`() = runTest {
        // given
        val id = UUID.randomUUID()

        // when
        val res = dao.findById(id)

        // then
        res shouldBe null
    }

    @Test
    fun `find all - not deleted accounts`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()
        val account1 = AccountEntity(
            id = id1,
            name = "Bank",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 1.0,
            isDeleted = false
        )
        val accounts = listOf(
            account1,
            AccountEntity(
                id = id2,
                name = "Cash",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 2.0,
                isDeleted = true
            ),
            AccountEntity(
                id = id3,
                name = "Bank 2",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 3.0,
                isDeleted = false
            )
        )

        // when
        dao.saveMany(accounts)
        val res = dao.findAll(false)

        // then
        res shouldBe listOf(
            account1,
            AccountEntity(
                id = id3,
                name = "Bank 2",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 3.0,
                isDeleted = false
            )
        )
    }

    @Test
    fun `find all - deleted accounts`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val account2 = AccountEntity(
            id = id2,
            name = "Cash",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 2.0,
            isDeleted = true
        )
        val accounts = listOf(
            AccountEntity(
                id = id1,
                name = "Bank",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                isDeleted = false
            ),
            account2
        )

        // when
        dao.saveMany(accounts)
        val res = dao.findAll(true)

        // then
        res shouldBe listOf(account2)
    }

    @Test
    fun `find all - empty list`() = runTest {
        // given
        val accounts = emptyList<AccountEntity>()

        // when
        dao.saveMany(accounts)
        val res = dao.findAll(false)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find max order num of accounts`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val accounts = listOf(
            AccountEntity(
                id = id1,
                name = "Bank",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                isDeleted = false
            ),
            AccountEntity(
                id = id2,
                name = "Cash",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                isDeleted = false
            )
        )

        // when
        dao.saveMany(accounts)
        val res = dao.findMaxOrderNum()

        // then
        res shouldBe 1.0
    }

    @Test
    fun `find max order num of empty list of accounts`() = runTest {
        // given
        val accounts = emptyList<AccountEntity>()

        // when
        dao.saveMany(accounts)
        val res = dao.findMaxOrderNum()

        // then
        res shouldBe null
    }

    @Test
    fun `save - create new`() = runTest {
        // given
        val id = UUID.randomUUID()
        val account = AccountEntity(
            id = id,
            name = "Bank",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            isDeleted = false
        )

        // when
        dao.save(account)
        val res = dao.findById(account.id)

        // then
        res shouldBe account
    }

    @Test
    fun `save - update existing`() = runTest {
        // given
        val id = UUID.randomUUID()
        val account = AccountEntity(
            id = id,
            name = "Bank",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            isDeleted = false
        )

        // when
        dao.save(account)
        dao.save(account.copy(name = "Cash"))
        val res = dao.findById(id)

        // then
        res shouldBe AccountEntity(
            id = id,
            name = "Cash",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            isDeleted = false
        )
    }

    @Test
    fun `save many`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val accounts = listOf(
            AccountEntity(
                id = id1,
                name = "Bank",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                isDeleted = false
            ),
            AccountEntity(
                id = id2,
                name = "Cash",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 2.0,
                isDeleted = false
            )
        )

        // when
        dao.saveMany(accounts)
        val res = dao.findAll(false)

        // then
        res shouldBe accounts
    }

    @Test
    fun `flag deleted`() = runTest {
        // given
        val id = UUID.randomUUID()
        val account = AccountEntity(
            id = id,
            name = "Bank",
            currency = "BGN",
            color = 1,
            icon = null,
            includeInBalance = true,
            orderNum = 1.0,
            isDeleted = false
        )

        // when
        dao.save(account)
        dao.flagDeleted(id)
        val notDeleted = dao.findAll(false)
        val deleted = dao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe listOf(account.copy(isDeleted = true))
    }

    @Test
    fun `delete by id`() = runTest {
        // given
        val id = UUID.randomUUID()
        val accounts = listOf(
            AccountEntity(
                id = id,
                name = "Bank",
                currency = "BGN",
                color = 1,
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                isDeleted = false
            )
        )

        // when
        dao.saveMany(accounts)
        dao.deleteById(id)
        val res = dao.findAll(false)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `delete all`() = runTest {
        // given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        // when
        dao.saveMany(
            listOf(
                AccountEntity(
                    id = id1,
                    name = "Bank",
                    currency = "BGN",
                    color = 1,
                    icon = null,
                    includeInBalance = true,
                    orderNum = 1.0,
                    isDeleted = false
                ),
                AccountEntity(
                    id = id2,
                    name = "Cash",
                    currency = "BGN",
                    color = 1,
                    icon = null,
                    includeInBalance = true,
                    orderNum = 2.0,
                    isDeleted = true
                )
            )
        )
        dao.deleteAll()
        val notDeleted = dao.findAll(false)
        val deleted = dao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
}
