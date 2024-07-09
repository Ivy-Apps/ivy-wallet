package com.ivy.data.repository.mapper

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.repository.CurrencyRepository
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(TestParameterInjector::class)
class AccountMapperTest {

    private val currencyRepository = mockk<CurrencyRepository>(relaxed = true)

    private lateinit var mapper: AccountMapper

    @Before
    fun setup() {
        mapper = AccountMapper(currencyRepository = currencyRepository)
    }

    @Test
    fun `maps domain to entity`(
        @TestParameter includeInBalance: Boolean,
    ) {
        // given
        val account = Account(
            id = ModelFixtures.AccountId,
            name = NotBlankTrimmedString.unsafe("Test"),
            asset = AssetCode.unsafe("USD"),
            color = ColorInt(value = 42),
            icon = IconAsset.unsafe("icon"),
            includeInBalance = includeInBalance,
            orderNum = 3.14,
        )

        // when
        val entity = with(mapper) { account.toEntity() }

        // then
        entity shouldBe AccountEntity(
            name = "Test",
            currency = "USD",
            color = 42,
            icon = "icon",
            includeInBalance = includeInBalance,
            orderNum = 3.14,
            isSynced = true,
            isDeleted = false,
            id = ModelFixtures.AccountId.value,
        )
    }

    // region entity to domain
    @Test
    fun `maps entity to domain - valid entity`(
        @TestParameter includeInBalance: Boolean,
        @TestParameter removed: Boolean,
    ) = runTest {
        // given
        val entity = ValidEntity.copy(
            orderNum = 42.0,
            includeInBalance = includeInBalance,
            isDeleted = removed,
        )

        // when
        val result = with(mapper) { entity.toDomain() }

        // then
        if (removed) {
            result.shouldBeLeft()
        } else {
            result.shouldBeRight() shouldBe Account(
                id = AccountId(entity.id),
                name = NotBlankTrimmedString.unsafe("Test"),
                asset = AssetCode.unsafe("USD"),
                color = ColorInt(value = 42),
                icon = IconAsset.unsafe("icon"),
                includeInBalance = includeInBalance,
                orderNum = 42.0,
            )
        }
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
        result.shouldBeRight().icon shouldBe null
    }

    @Test
    fun `maps entity to domain - invalid icon is okay`() = runTest {
        // given
        val invalidIconEntity = ValidEntity.copy(icon = "invalid icon")

        // when
        val result = with(mapper) { invalidIconEntity.toDomain() }

        // then
        result.shouldBeRight().icon shouldBe null
    }
    // endregion

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
