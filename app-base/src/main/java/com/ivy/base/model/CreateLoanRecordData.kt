package com.ivy.wallet.domain.deprecated.logic.model

import com.ivy.data.AccountOld
import java.time.LocalDateTime

data class CreateLoanRecordData(
    val note: String?,
    val amount: Double,
    val dateTime: LocalDateTime,
    val interest: Boolean = false,
    val account: AccountOld? = null,
    val createLoanRecordTransaction: Boolean = false,
    val convertedAmount: Double? = null
)