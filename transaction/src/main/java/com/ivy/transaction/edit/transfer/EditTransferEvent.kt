package com.ivy.transaction.edit.transfer

import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.Value
import com.ivy.data.transaction.TrnTime

sealed interface EditTransferEvent {
    data class Initial(val batchId: String) : EditTransferEvent
    object Save : EditTransferEvent
    object Close : EditTransferEvent
    object Delete : EditTransferEvent

    data class FromAmountChange(val amount: Value) : EditTransferEvent
    data class ToAmountChange(val amount: Value) : EditTransferEvent
    data class TitleChange(val title: String) : EditTransferEvent
    data class DescriptionChange(val description: String?) : EditTransferEvent
    data class FromAccountChange(val account: AccountUi) : EditTransferEvent
    data class ToAccountChange(val account: AccountUi) : EditTransferEvent
    data class CategoryChange(val category: CategoryUi?) : EditTransferEvent
    data class TrnTimeChange(val time: TrnTime) : EditTransferEvent
    data class FeeChange(val value: Value?) : EditTransferEvent
    data class FeePercent(val percent: Double) : EditTransferEvent
    data class RateChange(val newRate: Double) : EditTransferEvent
}