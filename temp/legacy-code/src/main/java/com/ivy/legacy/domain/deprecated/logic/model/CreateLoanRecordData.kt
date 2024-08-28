package com.ivy.wallet.domain.deprecated.logic.model

import com.ivy.base.model.LoanRecordType
import com.ivy.legacy.datamodel.Account
import java.time.Instant

data class CreateLoanRecordData(
    val note: String?,
    val amount: Double,
    val dateTime: Instant,
    val interest: Boolean = false,
    val account: Account? = null,
    val createLoanRecordTransaction: Boolean = false,
    val convertedAmount: Double? = null,
    val loanRecordType: LoanRecordType
)
