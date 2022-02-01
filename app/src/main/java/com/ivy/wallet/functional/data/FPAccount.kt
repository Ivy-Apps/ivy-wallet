package com.ivy.wallet.functional.data

import arrow.core.Option
import arrow.core.toOption
import com.ivy.wallet.model.entity.Account
import java.util.*

data class FPAccount(
    val id: UUID,
    val name: String,
    val currencyCode: Option<String>,
    val color: Int,
    val icon: Option<String>,
    val orderNum: Double,
    val includeInBalance: Boolean,
)

fun Account.toFPAccount(): FPAccount =
    FPAccount(
        id = id,
        name = name,
        currencyCode = currency.toOption(),
        color = color,
        icon = icon.toOption(),
        orderNum = orderNum,
        includeInBalance = includeInBalance
    )