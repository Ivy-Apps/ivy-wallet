package com.ivy.data.transaction

import com.ivy.data.AccountOld

data class TransferInfo(
    val toAccountId: AccountOld,
    val toAmount: Double
)