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
    val newRepository = FakeAccountRepository()

    "find by id" - {
        "valid id" {
            // given
            val repository = newRepository
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

        "invalid id" {
            // given
            val repository = newRepository
            val id = AccountId(UUID.randomUUID())

            // when
            val res = repository.findById(id)

            // then
            res shouldBe null
        }
    }
})