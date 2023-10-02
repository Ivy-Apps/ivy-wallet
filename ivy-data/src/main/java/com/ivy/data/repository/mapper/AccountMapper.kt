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
import java.time.Instant
import javax.inject.Inject

class AccountMapper @Inject constructor() {
    fun AccountEntity.toDomain(): Either<String, Account> = either {
        Account(
            id = AccountId(id),
            name = NotBlankTrimmedString.from(name).bind(),
            asset = AssetCode.from(currency ?: "").bind(),
            color = ColorInt(color),
            icon = icon?.let { IconAsset.from(it).getOrNull() },
            includeInBalance = includeInBalance,
            orderNum = orderNum,
            lastUpdated = Instant.EPOCH, // TODO: Wire this
            removed = isDeleted,
        )
    }

    fun Account.toEntity(): AccountEntity {
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