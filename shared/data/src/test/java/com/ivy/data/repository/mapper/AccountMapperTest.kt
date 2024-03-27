package com.ivy.data.repository.mapper

import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CurrencyRepository
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

class AccountMapperTest {

    private val currencyRepository = mockk<CurrencyRepository>(relaxed = true)

    private lateinit var mapper: AccountMapper

    @Before
    fun setup() {
        mapper = AccountMapper(currencyRepository = currencyRepository)
    }

    @Test
    fun `maps domain to entity`() {
        // given
        val id = UUID.randomUUID()
        val account = Account(
            id = AccountId(id),
            name = NotBlankTrimmedString.unsafe("Test"),
            asset = AssetCode.unsafe("USD"),
            color = ColorInt(value = 42),
            icon = IconAsset.unsafe("icon"),
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

    @Test
    fun `maps entity to domain - valid entity`() = runTest {
        // given
        val entity = ValidEntity

        // when
        val result = with(mapper) { entity.toDomain() }

        // then
        result.shouldBeRight() shouldBe Account(
            id = AccountId(entity.id),
            name = NotBlankTrimmedString.unsafe("Test"),
            asset = AssetCode.unsafe("USD"),
            color = ColorInt(value = 42),
            icon = IconAsset.unsafe("icon"),
            includeInBalance = true,
            orderNum = 0.0,
            lastUpdated = Instant.EPOCH,
            removed = false
        )
    }

    @Test
    fun `maps entity to domain - name missing`() = runTest {
        // given
        val corruptedEntity = ValidEntity.copy(name = "")

        // when
        val result = with(mapper) { corruptedEntity.toDomain() }

        // then
        result.shouldBeLeft()
    }

    @Test
    fun `maps entity to domain - currency missing, fallbacks to base currency`() = runTest {
        // given
        val corruptedEntity = ValidEntity.copy(currency = null)
        coEvery { currencyRepository.getBaseCurrency() } returns AssetCode.unsafe("BGN")

        // when
        val result = with(mapper) { corruptedEntity.toDomain() }

        // then
        result.shouldBeRight().asset shouldBe AssetCode.unsafe("BGN")
    }

    @Test
    fun `maps entity to domain - missing icon is okay`() = runTest {
        // given
        val missingIconEntity = ValidEntity.copy(icon = null)

        // when
        val result = with(mapper) { missingIconEntity.toDomain() }

        // then
        result.shouldBeRight()
    }

    @Test
    fun `maps entity to domain - invalid icon is okay`() = runTest {
        // given
        val invalidIconEntity = ValidEntity.copy(icon = "invalid icon")

        // when
        val result = with(mapper) { invalidIconEntity.toDomain() }

        // then
        result.shouldBeRight()
    }

    companion object {
        val ValidEntity = AccountEntity(
            name = "Test",
            currency = "USD",
            color = 42,
            icon = "icon",
            includeInBalance = true,
            isSynced = true,
            isDeleted = false,
            id = UUID.randomUUID(),
        )
    }
}
