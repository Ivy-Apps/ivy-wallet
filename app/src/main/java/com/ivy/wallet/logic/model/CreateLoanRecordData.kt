package com.ivy.wallet.logic.model

import com.ivy.wallet.model.entity.Account
import java.time.LocalDateTime

data class CreateLoanRecordData(
    val note: String?,
    val amount: Double,
    val dateTime: LocalDateTime,
    val interest: Boolean = false,
    val account: Account? = null,
    val createLoanRecordTransaction: Boolean = false,
    val convertedAmount: Double? = null
)