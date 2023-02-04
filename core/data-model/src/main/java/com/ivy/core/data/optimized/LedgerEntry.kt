package com.ivy.core.data.optimized

import com.ivy.core.data.AccountValue
import com.ivy.core.data.common.Value
import java.time.LocalDateTime

sealed interface LedgerEntry {
    val time: LocalDateTime

    sealed interface Single : LedgerEntry {
        data class Income(
            val value: Value,
            override val time: LocalDateTime,
        ) : Single

        data class Expense(
            val value: Value,
            override val time: LocalDateTime,
        ) : Single

    }

    data class Transfer(
        val from: AccountValue,
        val to: AccountValue,
        override val time: LocalDateTime,
    ) : LedgerEntry
}