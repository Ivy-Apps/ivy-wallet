package com.ivy.wallet.domain.pure.data

import arrow.core.Option
import arrow.core.toOption
import com.ivy.wallet.domain.data.entity.Account
import java.util.*

data class FPAccount(
    val id: UUID,
    val name: String,
    val currencyCode: String,
    val color: Int,
    val icon: Option<String>,
    val orderNum: Double,
    val includeInBalance: Boolean,
)

fun Account.toFPAccount(
    baseCurrencyCode: String
): FPAccount =
    FPAccount(
        id = id,
        name = name,
        currencyCode = currency ?: baseCurrencyCode,
        color = color,
        icon = icon.toOption(),
        orderNum = orderNum,
        includeInBalance = includeInBalance
    )