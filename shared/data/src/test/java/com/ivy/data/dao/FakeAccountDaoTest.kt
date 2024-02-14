package com.ivy.data.dao

import com.ivy.data.db.dao.fake.FakeAccountDao
import com.ivy.data.db.entity.AccountEntity
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class FakeAccountDaoTest : FreeSpec({
    fun newAccountDao() = FakeAccountDao()

    "find by id" - {
        "existing id" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.save(account)
            val res = accountDao.findById(id)

            // then
            res shouldBe account
        }

        "not existing id" {
            // given
            val accountDao = newAccountDao()
            val id = UUID.randomUUID()

            // when
            val res = accountDao.findById(id)

            // then
            res shouldBe null
        }
    }

    "find all" - {
        "not deleted accounts" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.saveMany(accounts)
            val res = accountDao.findAll(false)

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

        "deleted accounts" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.saveMany(accounts)
            val res = accountDao.findAll(true)

            // then
            res shouldBe listOf(account2)
        }

        "empty list" {
            // given
            val accountDao = newAccountDao()
            val accounts = emptyList<AccountEntity>()

            // when
            accountDao.saveMany(accounts)
            val res = accountDao.findAll(false)

            // then
            res shouldBe emptyList()
        }
    }

    "find max order num" - {
        "of accounts" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.saveMany(accounts)
            val res = accountDao.findMaxOrderNum()

            // then
            res shouldBe 1.0
        }

        "of empty list of accounts" {
            // given
            val accountDao = newAccountDao()
            val accounts = emptyList<AccountEntity>()

            // when
            accountDao.saveMany(accounts)
            val res = accountDao.findMaxOrderNum()

            // then
            res shouldBe null
        }
    }

    "save" - {
        "create new" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.save(account)
            val res = accountDao.findById(account.id)

            // then
            res shouldBe account
        }

        "update existing" {
            // given
            val accountDao = newAccountDao()
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
            accountDao.save(account)
            accountDao.save(account.copy(name = "Cash"))
            val res = accountDao.findById(id)

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
    }

    "save many" {
        // given
        val accountDao = newAccountDao()
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
        accountDao.saveMany(accounts)
        val res = accountDao.findAll(false)

        // then
        res shouldBe accounts
    }

    "flag deleted" {
        // given
        val accountDao = newAccountDao()
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
        accountDao.save(account)
        accountDao.flagDeleted(id)
        val notDeleted = accountDao.findAll(false)
        val deleted = accountDao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe listOf(account.copy(isDeleted = true))
    }

    "delete by id" {
        // given
        val accountDao = newAccountDao()
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
        accountDao.saveMany(accounts)
        accountDao.deleteById(id)
        val res = accountDao.findAll(false)

        // then
        res shouldBe emptyList()
    }

    "delete all" {
        // given
        val accountDao = newAccountDao()
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        // when
        accountDao.saveMany(
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
        accountDao.deleteAll()
        val notDeleted = accountDao.findAll(false)
        val deleted = accountDao.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
})
