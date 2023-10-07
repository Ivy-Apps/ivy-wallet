package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class AccountMapperTest : FreeSpec({

    "maps Account to entity" {
        // given
        val mapper = AccountMapper()
        val id = UUID.randomUUID()
        val account = Account(
            id = AccountId(id),
            name = NotBlankTrimmedString("Test"),
            asset = AssetCode("USD"),
            color = ColorInt(value = 42),
            icon = IconAsset("icon"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )

        // when
        val entity = with(mapper) { account.toEntity() }

        // then
        entity shouldBe AccountEntity(
            name = "Test",
            currency = "USD",
            color = 42,
            icon = "icon",
            includeInBalance = true,
            isSynced = true,
            isDeleted = false,
            id = id,
        )
    }

    "maps AccountEntity to domain" - {
        // given
        val mapper = AccountMapper()
        val entity = AccountEntity(
            name = "Test",
            currency = "USD",
            color = 42,
            icon = "icon",
            includeInBalance = true,
            isSynced = true,
            isDeleted = false,
            id = UUID.randomUUID(),
        )

        "valid entity" {
            // when
            val result = with(mapper) { entity.toDomain() }

            // then
            result.shouldBeRight() shouldBe Account(
                id = AccountId(entity.id),
                name = NotBlankTrimmedString("Test"),
                asset = AssetCode("USD"),
                color = ColorInt(value = 42),
                icon = IconAsset("icon"),
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.EPOCH,
                removed = false
            )
        }

        "name missing" {
            // given
            val corruptedEntity = entity.copy(name = "")

            // when
            val result = with(mapper) { corruptedEntity.toDomain() }

            // then
            result.shouldBeLeft()
        }

        "currency missing" {
            // given
            val corruptedEntity = entity.copy(currency = null)

            // when
            val result = with(mapper) { corruptedEntity.toDomain() }

            // then
            result.shouldBeLeft()
        }

        "missing icon is okay" {
            // given
            val missingIconEntity = entity.copy(icon = null)

            // when
            val result = with(mapper) { missingIconEntity.toDomain() }

            // then
            result.shouldBeRight()
        }
    }
})