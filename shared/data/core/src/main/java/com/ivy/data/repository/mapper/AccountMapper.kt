package com.ivy.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CurrencyRepository
import java.time.Instant
import javax.inject.Inject

class AccountMapper @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun AccountEntity.toDomain(): Either<String, com.ivy.data.model.Account> = either {
        com.ivy.data.model.Account(
            id = com.ivy.data.model.AccountId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            asset = currency?.let(AssetCode::from)?.bind()
                ?: currencyRepository.getBaseCurrency(),
            color = ColorInt(color),
            icon = icon?.let(IconAsset::from)?.getOrNull(),
            includeInBalance = includeInBalance,
            orderNum = orderNum,
            lastUpdated = Instant.EPOCH, // TODO: Wire this
            removed = isDeleted,
        )
    }

    fun com.ivy.data.model.Account.toEntity(): AccountEntity {
        return AccountEntity(
            name = name.value,
            currency = asset.code,
            color = color.value,
            icon = icon?.id,
            orderNum = orderNum,
            includeInBalance = includeInBalance,
            isDeleted = removed,
            id = id.value,
            isSynced = true, // TODO: Delete this
        )
    }
}
