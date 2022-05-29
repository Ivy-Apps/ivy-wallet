package com.ivy.wallet.ui.data

import java.math.BigDecimal

data class BufferInfo(
    val amount: BigDecimal,
    val bufferDiff: BigDecimal
)