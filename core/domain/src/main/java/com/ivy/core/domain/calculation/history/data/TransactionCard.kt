package com.ivy.core.domain.calculation.history.data

import com.ivy.core.data.Transaction

data class TransactionCard(
    val transaction: Transaction,
) : TransactionListItem