package com.ivy.wallet.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "loan_records")
data class LoanRecord(
    val loanId: UUID,
    val amount: Double,
    val note: String?,
    val dateTime: LocalDateTime,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)