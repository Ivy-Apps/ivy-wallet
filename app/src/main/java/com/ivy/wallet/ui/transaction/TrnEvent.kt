package com.ivy.wallet.ui.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction

sealed class TrnEvent {
    data class NewTransaction(
        val type: TransactionType
    ) : TrnEvent()

    data class LoadTransaction(
        val transaction: Transaction
    ) : TrnEvent()
}