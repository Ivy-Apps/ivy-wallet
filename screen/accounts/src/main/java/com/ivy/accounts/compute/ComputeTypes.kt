package com.ivy.accounts.compute

import com.ivy.data.model.Account

sealed interface ComputeTypes {
    data class FromAccount(
        val account: Account,
        //Used to compute scoped summations with in the time range (e.g Monthly Income/Expense)
        val scopedTimeRange: ScopedTimeRange
    ) : ComputeTypes
}