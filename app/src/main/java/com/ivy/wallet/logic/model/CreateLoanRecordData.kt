package com.ivy.wallet.logic.model

import java.time.LocalDateTime

data class CreateLoanRecordData(
    val note: String?,
    val amount: Double,
    val dateTime: LocalDateTime,
)