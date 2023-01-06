package com.ivy.transaction.create.transfer

import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.Value
import com.ivy.data.transaction.TrnTime

sealed interface NewTransferEvent {
    object Initial : NewTransferEvent
    object Add : NewTransferEvent
    object Close : NewTransferEvent

    data class TransferAmountChange(val amount: Value) : NewTransferEvent
    data class FromAmountChange(val amount: Value) : NewTransferEvent
    data class ToAmountChange(val amount: Value) : NewTransferEvent
    data class TitleChange(val title: String) : NewTransferEvent
    data class DescriptionChange(val description: String?) : NewTransferEvent
    data class FromAccountChange(val account: AccountUi) : NewTransferEvent
    data class ToAccountChange(val account: AccountUi) : NewTransferEvent
    data class CategoryChange(val category: CategoryUi?) : NewTransferEvent
    data class TrnTimeChange(val time: TrnTime) : NewTransferEvent
    data class FeePercent(val percent: Double) : NewTransferEvent
    data class FeeChange(val value: Value?) : NewTransferEvent
    data class RateChange(val newRate: Double) : NewTransferEvent
}