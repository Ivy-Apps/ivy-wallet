package com.ivy.data.testing

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class FakeAccountRepositoryTest : FreeSpec({
    fun newRepository() = FakeAccountRepository()

    "find by id" - {
        "existing id" {
            // given
            val repository = newRepository()
            val id = AccountId(UUID.randomUUID())
            val account = Account(
                id = id,
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )

            // when
            repository.save(account)
            val res = repository.findById(id)

            // then
            res shouldBe account
        }

        "not existing id" {
            // given
            val repository = newRepository()
            val id = AccountId(UUID.randomUUID())

            // when
            val res = repository.findById(id)

            // then
            res shouldBe null
        }
    }

    "find all" - {
        "not deleted accounts" {
            // given
            val repository = newRepository()
            val id1 = AccountId(UUID.randomUUID())
            val id2 = AccountId(UUID.randomUUID())
            val account1 = Account(
                id = id1,
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
            val accounts = listOf(
                account1,
                Account(
                    id = id2,
                    name = NotBlankTrimmedString("Cash"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 2.0,
                    lastUpdated = Instant.EPOCH,
                    removed = true
                )
            )

            // when
            repository.saveMany(accounts)
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(account1)
        }

        "deleted accounts" {
            // given
            val repository = newRepository()
            val id1 = AccountId(UUID.randomUUID())
            val id2 = AccountId(UUID.randomUUID())
            val account2 = Account(
                id = id2,
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 2.0,
                lastUpdated = Instant.EPOCH,
                removed = true
            )
            val accounts = listOf(
                Account(
                    id = id1,
                    name = NotBlankTrimmedString("Bank"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 1.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                ),
                account2
            )

            // when
            repository.saveMany(accounts)
            val res = repository.findAll(true)

            // then
            res shouldBe listOf(account2)
        }

        "empty list" {
            // given
            val repository = newRepository()
            val accounts = emptyList<Account>()

            // when
            repository.saveMany(accounts)
            val res = repository.findAll(false)

            // then
            res shouldBe emptyList()
        }
    }

    "find max order num" - {
        "of accounts" {
            // given
            val repository = newRepository()
            val id1 = AccountId(UUID.randomUUID())
            val id2 = AccountId(UUID.randomUUID())
            val accounts = listOf(
                Account(
                    id = id1,
                    name = NotBlankTrimmedString("Bank"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 0.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                ),
                Account(
                    id = id2,
                    name = NotBlankTrimmedString("Cash"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 1.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                )
            )

            // when
            repository.saveMany(accounts)
            val res = repository.findMaxOrderNum()

            // then
            res shouldBe 1.0
        }

        "of empty list of accounts" {
            // given
            val repository = newRepository()
            val accounts = emptyList<Account>()

            // when
            repository.saveMany(accounts)
            val res = repository.findMaxOrderNum()

            // then
            res shouldBe 0.0
        }
    }

    "save" {
        // given
        val repository = newRepository()
        val id = AccountId(UUID.randomUUID())
        val account = Account(
            id = id,
            name = NotBlankTrimmedString("Bank"),
            asset = AssetCode("BGN"),
            color = ColorInt(1),
            icon = null,
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        // when
        repository.save(account)
        val res = repository.findById(account.id)

        // then
        res shouldBe account
    }

    "save many" {
        // given
        val repository = newRepository()
        val id1 = AccountId(UUID.randomUUID())
        val id2 = AccountId(UUID.randomUUID())
        val accounts = listOf(
            Account(
                id = id1,
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            ),
            Account(
                id = id2,
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 2.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
        )

        // when
        val res = repository.saveMany(accounts)

        // then
        res shouldBe accounts
    }

    "flag deleted" {
        // given
        val repository = newRepository()
        val id = AccountId(UUID.randomUUID())

        // when
        val res = repository.flagDeleted(id)

        // then
        res shouldBe id
    }

    "delete by id" {
        // given
        val repository = newRepository()
        val id = AccountId(UUID.randomUUID())
        val accounts = listOf(
            Account(
                id = id,
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
        )

        // when
        val res = repository.deleteById(id)

        // then
        res shouldBe id
    }

    "delete all" {
        // given
        val repository = newRepository()

        // when
        repository.deleteAll()
        val res = repository.findAll(false)

        // then
        res shouldBe emptyList()
    }
})