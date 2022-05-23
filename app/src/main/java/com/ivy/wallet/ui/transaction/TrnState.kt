package com.ivy.wallet.ui.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class TrnState {
    object Initial : TrnState()

    data class NewTransaction(
        val type: TransactionType,
        val account: Account,
        val toAccount: Account?,
        val amount: BigDecimal,
        val category: Category?,
        val dateTime: LocalDateTime,
        val title: String?,
        val description: String?
    ) : TrnState()

    data class EditTransaction(
        val transaction: Transaction
    ) : TrnState()
}