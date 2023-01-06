package com.ivy.transaction.edit.trn

import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime

sealed interface EditTrnEvent {
    data class Initial(val trnId: String) : EditTrnEvent
    object Delete : EditTrnEvent
    object Save : EditTrnEvent
    object Close : EditTrnEvent

    data class AmountChange(val amount: Value) : EditTrnEvent
    data class TitleChange(val title: String) : EditTrnEvent
    data class DescriptionChange(val description: String?) : EditTrnEvent
    data class AccountChange(val account: AccountUi) : EditTrnEvent
    data class CategoryChange(val category: CategoryUi?) : EditTrnEvent
    data class TrnTypeChange(val trnType: TransactionType) : EditTrnEvent
    data class TrnTimeChange(val time: TrnTime) : EditTrnEvent
    data class HiddenChange(val hidden: Boolean) : EditTrnEvent
}