package com.ivy.wallet.ui.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.ui.transaction.data.TrnDate
import com.ivy.wallet.ui.transaction.data.TrnExchangeRate
import java.math.BigDecimal

sealed class TrnState {
    object Initial : TrnState()

    data class NewTransaction(
        val type: TransactionType,
        val account: Account,
        val amount: BigDecimal,
        val category: Category?,
        val date: TrnDate,
        val title: String?,
        val description: String?,

        //Transfers
        val toAccount: Account?,
        val toAmount: BigDecimal?,
        val exchangeRate: TrnExchangeRate?,
        //--------------------------

        val titleSuggestions: List<String>,

        val accounts: List<Account>,
        val categories: List<Category>
    ) : TrnState()

    data class EditTransaction(
        val transaction: Transaction,

        val titleSuggestions: List<String>,

        val accounts: List<Account>,
        val categories: List<Category>
    ) : TrnState()

    data class Invalid(val message: String) : TrnState()
}