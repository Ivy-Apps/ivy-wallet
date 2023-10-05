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
            val id3 = AccountId(UUID.randomUUID())
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
                ),
                Account(
                    id = id3,
                    name = NotBlankTrimmedString("Bank 2"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 3.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                )
            )

            // when
            repository.saveMany(accounts.sortedByDescending { it.orderNum })
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                account1,
                Account(
                    id = id3,
                    name = NotBlankTrimmedString("Bank 2"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(1),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 3.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                )
            )
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

    "save" - {
        "create new" {
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

        "update existing" {
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
            repository.save(account.copy(name = NotBlankTrimmedString("Cash")))
            val res = repository.findById(id)

            // then
            res shouldBe Account(
                id = id,
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
        }
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
        repository.saveMany(accounts)
        val res = repository.findAll(false)

        // then
        res shouldBe accounts
    }

    "flag deleted" {
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
        repository.flagDeleted(id)
        val notDeleted = repository.findAll(false)
        val deleted = repository.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe listOf(account.copy(removed = true))
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
        repository.saveMany(accounts)
        repository.deleteById(id)
        val res = repository.findAll(false)

        // then
        res shouldBe emptyList()
    }

    "delete all" {
        // given
        val repository = newRepository()
        val id1 = AccountId(UUID.randomUUID())
        val id2 = AccountId(UUID.randomUUID())

        // when
        repository.saveMany(
            listOf(
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
                    removed = true
                )
            )
        )
        repository.deleteAll()
        val notDeleted = repository.findAll(false)
        val deleted = repository.findAll(true)

        // then
        notDeleted shouldBe emptyList()
        deleted shouldBe emptyList()
    }
})