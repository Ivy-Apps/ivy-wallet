package com.ivy.base.data

import com.ivy.design.l0_system.Theme
import java.math.BigDecimal
import java.util.*

data class Settings(
    val theme: Theme,
    val baseCurrency: String,
    val bufferAmount: BigDecimal,
    val name: String,

    val id: UUID = UUID.randomUUID()
)