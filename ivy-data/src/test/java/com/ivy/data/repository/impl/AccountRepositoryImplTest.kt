package com.ivy.data.repository.impl

import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.data.source.LocalAccountDataSource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.time.Instant
import java.util.UUID

class AccountRepositoryImplTest : FreeSpec({
    val dataSource = mockk<LocalAccountDataSource>()

    fun newRepository(): AccountRepository = AccountRepositoryImpl(
        mapper = AccountMapper(),
        dataSource = dataSource
    )

    "find by id" - {
        "null AccountEntity" {
            // given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            coEvery { dataSource.findById(accountId.value) } returns null

            // when
            val res = repository.findById(accountId)

            // then
            res shouldBe null
        }

        "valid AccountEntity" {
            // given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            coEvery { dataSource.findById(accountId.value) } returns AccountEntity(
                name = "Bank",
                currency = "BGN",
                color = 1,
                icon = null,
                orderNum = 1.0,
                includeInBalance = true,
                isSynced = true,
                isDeleted = false,
                id = accountId.value
            )

            // when
            val res = repository.findById(accountId)

            // then
            res shouldBe Account(
                id = accountId,
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("BGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 1.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
        }

        "invalid AccountEntity" {
            // given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            coEvery { dataSource.findById(accountId.value) } returns AccountEntity(
                name = " ",
                currency = "BGN",
                color = 1,
                icon = null,
                orderNum = 2.0,
                includeInBalance = true,
                isSynced = true,
                isDeleted = false,
                id = accountId.value
            )

            // when
            val res = repository.findById(accountId)

            // then
            res shouldBe null
        }
    }

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

        "list of valid accounts" {
            // given
            val repository = newRepository()
            val account1Id = AccountId(UUID.randomUUID())
            val account2Id = AccountId(UUID.randomUUID())
            coEvery { dataSource.findAll(false) } returns listOf(
                AccountEntity(
                    name = "Bank",
                    currency = "BGN",
                    color = 1,
                    icon = null,
                    orderNum = 1.0,
                    includeInBalance = true,
                    isSynced = true,
                    isDeleted = false,
                    id = account1Id.value
                ),
                AccountEntity(
                    name = "Cash",
                    currency = "BGN",
                    color = 2,
                    icon = null,
                    orderNum = 2.0,
                    includeInBalance = true,
                    isSynced = true,
                    isDeleted = false,
                    id = account2Id.value
                )
            )

            // when
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                Account(
                    id = account1Id,
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
                    id = account2Id,
                    name = NotBlankTrimmedString("Cash"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(2),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 2.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                )
            )
        }

        "list with valid and invalid accounts" {
            // given
            val repository = newRepository()
            val account1Id = AccountId(UUID.randomUUID())
            val account2Id = AccountId(UUID.randomUUID())
            coEvery { dataSource.findAll(false) } returns listOf(
                AccountEntity(
                    name = "Bank",
                    currency = "BGN",
                    color = 1,
                    icon = null,
                    orderNum = 1.0,
                    includeInBalance = true,
                    isSynced = true,
                    isDeleted = false,
                    id = account1Id.value
                ),
                AccountEntity(
                    name = "  ",
                    currency = "BGN",
                    color = 2,
                    icon = null,
                    orderNum = 2.0,
                    includeInBalance = true,
                    isSynced = true,
                    isDeleted = false,
                    id = account2Id.value
                )
            )

            // when
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                Account(
                    id = account1Id,
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
        }
    }

    "finds max order num" - {
        "no accounts" {
            // given
            coEvery { dataSource.findMaxOrderNum() } returns null
            val repository = newRepository()

            // when
            val orderNum = repository.findMaxOrderNum()

            // then
            orderNum shouldBe 0.0
        }

        "existing account" {
            // given
            coEvery { dataSource.findMaxOrderNum() } returns 42.0
            val repository = newRepository()

            // when
            val orderNum = repository.findMaxOrderNum()

            // then
            orderNum shouldBe 42.0
        }
    }

    "save" {
        // given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        coEvery { dataSource.save(any()) } just runs

        // when
        repository.save(
            Account(
                id = accountId,
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

        // then
        coVerify(exactly = 1) {
            dataSource.save(
                AccountEntity(
                    name = "Bank",
                    currency = "BGN",
                    color = 1,
                    icon = null,
                    orderNum = 1.0,
                    includeInBalance = true,
                    isSynced = true,
                    isDeleted = false,
                    id = accountId.value
                )
            )
        }
    }

    "save many" {
        // given
        val repository = newRepository()
        val account1Id = AccountId(UUID.randomUUID())
        val account2Id = AccountId(UUID.randomUUID())
        coEvery { dataSource.saveMany(any()) } just runs

        // when
        repository.saveMany(
            listOf(
                Account(
                    id = account1Id,
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
                    id = account2Id,
                    name = NotBlankTrimmedString("Cash"),
                    asset = AssetCode("BGN"),
                    color = ColorInt(2),
                    icon = null,
                    includeInBalance = true,
                    orderNum = 2.0,
                    lastUpdated = Instant.EPOCH,
                    removed = false
                )
            )
        )

        // then
        coVerify(exactly = 1) {
            dataSource.saveMany(
                listOf(
                    AccountEntity(
                        name = "Bank",
                        currency = "BGN",
                        color = 1,
                        icon = null,
                        orderNum = 1.0,
                        includeInBalance = true,
                        isSynced = true,
                        isDeleted = false,
                        id = account1Id.value
                    ),
                    AccountEntity(
                        name = "Cash",
                        currency = "BGN",
                        color = 2,
                        icon = null,
                        orderNum = 2.0,
                        includeInBalance = true,
                        isSynced = true,
                        isDeleted = false,
                        id = account2Id.value
                    )
                )
            )
        }
    }

    "flag deleted" {
        // given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        coEvery { dataSource.flagDeleted(any()) } just runs

        // when
        repository.flagDeleted(accountId)

        // then
        coVerify(exactly = 1) {
            dataSource.flagDeleted(accountId.value)
        }
    }

    "delete by id" {
        // given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        coEvery { dataSource.deleteById(any()) } just runs

        // when
        repository.deleteById(accountId)

        // then
        coVerify(exactly = 1) {
            dataSource.deleteById(accountId.value)
        }
    }

    "delete all" {
        // given
        val repository = newRepository()
        coEvery { dataSource.deleteAll() } just runs

        // when
        repository.deleteAll()

        // then
        coVerify(exactly = 1) {
            dataSource.deleteAll()
        }
    }
})