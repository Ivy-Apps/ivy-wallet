package com.ivy.wallet.ui.transaction.data

import java.time.LocalDate
import java.time.LocalDateTime

sealed class TrnDate {
    data class ActualDate(val dateTime: LocalDateTime) : TrnDate()

    data class DueDate(val dueDate: LocalDate) : TrnDate()
}