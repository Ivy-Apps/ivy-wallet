package com.ivy.wallet.domain.data.core

import com.ivy.design.l0_system.Theme
import java.util.*

data class Settings(
    val theme: Theme,
    val currency: String,
    val bufferAmount: Double,
    val name: String,

    val id: UUID = UUID.randomUUID()
)