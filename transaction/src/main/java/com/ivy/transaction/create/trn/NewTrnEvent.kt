package com.ivy.transaction.create.trn

import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.navigation.destinations.transaction.NewTransaction

sealed interface NewTrnEvent {
    data class Initial(val arg: NewTransaction.Arg) : NewTrnEvent
    object Add : NewTrnEvent
    object Close : NewTrnEvent

    data class AmountChange(val amount: Value) : NewTrnEvent
    data class TitleChange(val title: String) : NewTrnEvent
    data class DescriptionChange(val description: String?) : NewTrnEvent
    data class AccountChange(val account: AccountUi) : NewTrnEvent
    data class CategoryChange(val category: CategoryUi?) : NewTrnEvent
    data class TrnTypeChange(val trnType: TransactionType) : NewTrnEvent
    data class TrnTimeChange(val time: TrnTime) : NewTrnEvent
}