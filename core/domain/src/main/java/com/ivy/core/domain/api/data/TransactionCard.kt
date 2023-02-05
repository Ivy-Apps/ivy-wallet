package com.ivy.core.domain.api.data

import com.ivy.core.data.Transaction

data class TransactionCard(
    val transaction: Transaction,
) : TransactionListItem