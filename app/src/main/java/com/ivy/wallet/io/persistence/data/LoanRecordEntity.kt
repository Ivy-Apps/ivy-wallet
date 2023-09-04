package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.core.LoanRecord
import java.time.LocalDateTime
import java.util.*

@Keep
@Entity(tableName = "loan_records")
data class LoanRecordEntity(
    @SerializedName("loanId")
    val loanId: UUID,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("note")
    val note: String? = null,
    @SerializedName("dateTime")
    val dateTime: LocalDateTime,
    @SerializedName("interest")
    val interest: Boolean = false,
    @SerializedName("accountId")
    val accountId: UUID? = null,
    // This is used store the converted amount for currencies which are different from the loan account currency
    @SerializedName("convertedAmount")
    val convertedAmount: Double? = null,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
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
