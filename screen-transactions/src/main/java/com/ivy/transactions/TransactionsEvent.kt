package com.ivy.transactions

import com.ivy.base.legacy.Transaction
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.navigation.TransactionsScreen
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData

sealed interface TransactionsEvent {
    data class SetUpcomingExpanded(val expanded: Boolean) : TransactionsEvent
    data class SetOverdueExpanded(val expanded: Boolean) : TransactionsEvent

    data class SetPeriod(
        val screen: TransactionsScreen,
        val period: TimePeriod
    ) : TransactionsEvent

    data class NextMonth(val screen: TransactionsScreen) : TransactionsEvent
    data class PreviousMonth(val screen: TransactionsScreen) : TransactionsEvent
    data class Delete(val screen: TransactionsScreen) : TransactionsEvent
    data class EditCategory(val updatedCategory: Category) : TransactionsEvent
    data class EditAccount(
        val screen: TransactionsScreen,
        val account: Account,
        val newBalance: Double
    ) : TransactionsEvent

    data class PayOrGet(
        val screen: TransactionsScreen,
        val transaction: Transaction
    ) : TransactionsEvent

    data class SkipTransaction(
        val screen: TransactionsScreen,
        val transaction: Transaction
    ) : TransactionsEvent

    data class SkipTransactions(
        val screen: TransactionsScreen,
        val transactions: List<Transaction>
    ) : TransactionsEvent

    data class UpdateAccountDeletionState(val confirmationText: String) : TransactionsEvent
    data class SetSkipAllModalVisible(val visible: Boolean) : TransactionsEvent
    data class OnDeleteModal1Visible(val delete: Boolean) : TransactionsEvent
    data class OnChoosePeriodModalData(val data: ChoosePeriodModalData?) : TransactionsEvent
}