package com.ivy.wallet.domain.data.core

import com.ivy.wallet.io.network.data.LoanRecordDTO
import com.ivy.wallet.io.persistence.data.LoanRecordEntity
import java.time.LocalDateTime
import java.util.*

data class LoanRecord(
    val loanId: UUID,
    val amount: Double,
    val note: String? = null,
    val dateTime: LocalDateTime,
    val interest: Boolean = false,
    val accountId: UUID? = null,
    //This is used store the converted amount for currencies which are different from the loan account currency
    val convertedAmount: Double? = null,

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
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )

    fun toDTO(): LoanRecordDTO = LoanRecordDTO(
        loanId = loanId,
        amount = amount,
        note = note,
        dateTime = dateTime,
        interest = interest,
        accountId = accountId,
        convertedAmount = convertedAmount,
        id = id
    )
}