package com.ivy.data.transaction

import com.ivy.data.Account

data class TransferInfo(
    val toAccountId: Account,
    val toAmount: Double
)