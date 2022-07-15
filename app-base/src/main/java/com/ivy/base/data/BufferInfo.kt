package com.ivy.base.data

import java.math.BigDecimal

data class BufferInfo(
    val amount: BigDecimal,
    val bufferDiff: BigDecimal
)