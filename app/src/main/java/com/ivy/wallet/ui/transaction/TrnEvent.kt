package com.ivy.wallet.ui.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

sealed class TrnEvent {
    data class NewTransaction(
        val type: TransactionType,
        val account: Account?,
        val category: Category?
    ) : TrnEvent()

    data class LoadTransaction(
        val transaction: Transaction
    ) : TrnEvent()

    // ---------------------------------
    data class AmountChanged(
        val newAmount: BigDecimal
    ) : TrnEvent()

    data class TitleChanged(
        val newTitle: String
    ) : TrnEvent()

    data class DescriptionChanged(
        val newDesc: String
    ) : TrnEvent()

    data class AccountChanged(
        val newAccount: Account
    ) : TrnEvent()

    data class CategoryChanged(
        val newCategory: Category
    ) : TrnEvent()

    data class DateChanged(
        val dateTime: LocalDateTime
    ) : TrnEvent()

    data class DueChanged(
        val dueDateTime: LocalDate
    ) : TrnEvent()

    data class TypeChanged(
        val type: TransactionType
    ) : TrnEvent()

    data class ToAccountChanged(
        val account: Account
    ) : TrnEvent()

    data class SetExchangeRate(
        val exchangeRate: BigDecimal
    ) : TrnEvent()

    //----------------------------

    object Save : TrnEvent()

    object LoadTitleSuggestions : TrnEvent()
}