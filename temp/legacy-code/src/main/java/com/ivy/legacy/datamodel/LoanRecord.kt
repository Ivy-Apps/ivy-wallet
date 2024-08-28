package com.ivy.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.base.model.LoanRecordType
import com.ivy.data.db.entity.LoanRecordEntity
import java.time.Instant
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class LoanRecord(
    val loanId: UUID,
    val amount: Double,
    val note: String? = null,
    val dateTime: Instant,
    val interest: Boolean = false,
    val accountId: UUID? = null,
    // This is used store the converted amount for currencies which are different from the loan account currency
    val convertedAmount: Double? = null,
    val loanRecordType: LoanRecordType,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): LoanRecordEntity = LoanRecordEntity(
        loanId = loanId,
        amount = amount,
        note = note,
        dateTime = dateTime,
        interest = interest,
        accountId = accountId,
        convertedAmount = convertedAmount,
        loanRecordType = loanRecordType,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
