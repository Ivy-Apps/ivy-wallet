package com.ivy.core.ui.transaction.handling

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivy.core.domain.HandlerViewModel
import com.ivy.core.ui.algorithm.trnhistory.data.TransactionUi
import com.ivy.core.ui.algorithm.trnhistory.data.TransferUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Immutable
data class TrnItemClickHandler(
    val onTrnClick: (TransactionUi) -> Unit,
    val onTransferClick: (TransferUi) -> Unit,
    val onAccountClick: (AccountUi) -> Unit,
    val onCategoryClick: (CategoryUi) -> Unit,
)

sealed interface TrnItemClickEvent {
    data class TransactionClick(val trn: TransactionUi) : TrnItemClickEvent
    data class TransferClick(val transfer: TransferUi) : TrnItemClickEvent
    data class AccountClick(val account: AccountUi) : TrnItemClickEvent
    data class CategoryClick(val category: CategoryUi) : TrnItemClickEvent
}

@HiltViewModel
class TrnItemClickHandlerViewModel @Inject constructor(
    private val navigator: Navigator
) : HandlerViewModel<TrnItemClickEvent>() {
    override suspend fun handleEvent(event: TrnItemClickEvent) = when (event) {
        is TrnItemClickEvent.AccountClick -> handleAccountClick(event.account)
        is TrnItemClickEvent.CategoryClick -> handleCategoryClick(event.category)
        is TrnItemClickEvent.TransactionClick -> handleTransactionClick(event.trn)
        is TrnItemClickEvent.TransferClick -> handleTransferClick(event.transfer)
    }

    private fun handleAccountClick(account: AccountUi) {
        navigator.navigate(Destination.accountTransactions.destination(account.id))
    }

    private fun handleCategoryClick(category: CategoryUi) {
        navigator.navigate(Destination.categoryTransactions.destination(category.id))
    }

    private fun handleTransactionClick(transaction: TransactionUi) {
        navigator.navigate(Destination.transaction.destination(transaction.id))
    }

    private fun handleTransferClick(transfer: TransferUi) {
        navigator.navigate(Destination.transfer.destination(transfer.batchId))
    }
}

@Composable
fun defaultTrnItemClickHandler(): TrnItemClickHandler {
    val viewModel: TrnItemClickHandlerViewModel? = hiltViewModelPreviewSafe()

    return TrnItemClickHandler(
        onAccountClick = {
            viewModel?.onEvent(TrnItemClickEvent.AccountClick(it))
        },
        onCategoryClick = {
            viewModel?.onEvent(TrnItemClickEvent.CategoryClick(it))
        },
        onTrnClick = {
            viewModel?.onEvent(TrnItemClickEvent.TransactionClick(it))
        },
        onTransferClick = {
            viewModel?.onEvent(TrnItemClickEvent.TransferClick(it))
        }
    )
}