package com.ivy.data

import java.math.BigDecimal
import java.util.*

@Deprecated("won't use")
data class Settings(
    val theme: ThemeOld,
    val baseCurrency: CurrencyCode,
    val bufferAmount: BigDecimal,
    val name: String,

    val id: UUID = UUID.randomUUID()
)