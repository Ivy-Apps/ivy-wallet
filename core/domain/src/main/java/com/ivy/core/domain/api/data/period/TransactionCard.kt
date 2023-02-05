package com.ivy.core.domain.api.data.period

import com.ivy.core.data.Transaction

data class TransactionCard(
    val transaction: Transaction,
) : TransactionListItem