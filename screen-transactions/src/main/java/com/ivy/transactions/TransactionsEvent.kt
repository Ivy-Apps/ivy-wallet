package com.ivy.transactions

import com.ivy.base.legacy.Transaction
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.navigation.ItemStatisticScreen

sealed interface TransactionsEvent {
    data class SetUpcomingExpanded(val expanded: Boolean) : TransactionsEvent
    data class SetOverdueExpanded(val expanded: Boolean) : TransactionsEvent

    data class SetPeriod(
        val screen: ItemStatisticScreen,
        val period: TimePeriod
    ) : TransactionsEvent

    data class NextMonth(val screen: ItemStatisticScreen) : TransactionsEvent
    data class PreviousMonth(val screen: ItemStatisticScreen) : TransactionsEvent
    data class Delete(val screen: ItemStatisticScreen) : TransactionsEvent
    data class EditCategory(val updatedCategory: Category) : TransactionsEvent
    data class EditAccount(
        val screen: ItemStatisticScreen,
        val account: Account,
        val newBalance: Double
    ) : TransactionsEvent

    data class PayOrGet(
        val screen: ItemStatisticScreen,
        val transaction: Transaction
    ) : TransactionsEvent

    data class SkipTransaction(
        val screen: ItemStatisticScreen,
        val transaction: Transaction
    ) : TransactionsEvent

    data class SkipTransactions(
        val screen: ItemStatisticScreen,
        val transactions: List<Transaction>
    ) : TransactionsEvent

    data class UpdateAccountDeletionState(val confirmationText: String) : TransactionsEvent
}