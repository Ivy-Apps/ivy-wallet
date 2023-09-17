package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.LoanRecord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Keep
@Serializable
@Entity(tableName = "loan_records")
data class LoanRecordEntity(
    @SerialName("loanId")
    val loanId: UUID,
    @SerialName("amount")
    val amount: Double,
    @SerialName("note")
    val note: String? = null,
    @SerialName("dateTime")
    val dateTime: LocalDateTime,
    @SerialName("interest")
    val interest: Boolean = false,
    @SerialName("accountId")
    val accountId: UUID? = null,
    // This is used store the converted amount for currencies which are different from the loan account currency
    @SerialName("convertedAmount")
    val convertedAmount: Double? = null,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
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
