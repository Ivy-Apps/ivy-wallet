package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.loan.LoanRecord
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "loan_records")
data class LoanRecordEntity(
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

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): LoanRecord = LoanRecord(
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
}

fun LoanRecord.toEntity(): LoanRecordEntity = LoanRecordEntity(
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