package com.ivy.wallet.ui.transaction

import arrow.core.NonEmptyList
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.mapError
import com.ivy.frp.monad.mapSuccess
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.viewmodel.transaction.SaveTrnLocallyAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.ui.transaction.data.TrnDate
import com.ivy.wallet.utils.timeNowUTC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val saveTrnLocallyAct: SaveTrnLocallyAct
) : FRPViewModel<TrnState, TrnEvent>() {
    override val _state: MutableStateFlow<TrnState> = MutableStateFlow(TrnState.Initial)

    override suspend fun handleEvent(event: TrnEvent): suspend () -> TrnState = when (event) {
        is TrnEvent.NewTransaction -> newTransaction(event)
        is TrnEvent.LoadTransaction -> loadTransaction(event)
        is TrnEvent.AccountChanged -> TODO()
        is TrnEvent.AmountChanged -> TODO()
        is TrnEvent.CategoryChanged -> TODO()
        is TrnEvent.DateChanged -> TODO()
        is TrnEvent.DescriptionChanged -> TODO()
        is TrnEvent.DueChanged -> TODO()
        is TrnEvent.TitleChanged -> TODO()
        is TrnEvent.ToAccountChanged -> TODO()
        is TrnEvent.TypeChanged -> TODO()
        is TrnEvent.SetExchangeRate -> TODO()
        is TrnEvent.Save -> TODO()
        TrnEvent.LoadTitleSuggestions -> TODO()
    }

    private suspend fun newTransaction(event: TrnEvent.NewTransaction) = ::loadRequiredData then {
        if (it.first.isEmpty()) {
            Res.Err("No accounts created")
        } else {
            Res.Ok(
                Pair(NonEmptyList.fromListUnsafe(it.first), it.second)
            )
        }
    } mapError { errMsg ->
        TrnState.Invalid(message = errMsg)
    } mapSuccess { (accounts, categories) ->
        TrnState.NewTransaction(
            type = event.type,
            account = event.account ?: accounts.head,
            amount = BigDecimal.ZERO,
            date = TrnDate.ActualDate(timeNowUTC()),
            category = event.category,
            title = null,
            description = null,

            //TODO: Handle transfers properly
            toAccount = null,
            toAmount = null,
            exchangeRate = null,
            //TODO: Handle transfers properly

            titleSuggestions = emptyList(),

            accounts = accounts,
            categories = categories
        )
    } then {
        when (it) {
            is Res.Ok -> it.data
            is Res.Err -> it.error
        }
    }

    private suspend fun loadTransaction(event: TrnEvent.LoadTransaction) =
        ::loadRequiredData then { (accounts, categories) ->
            TrnState.EditTransaction(
                transaction = event.transaction,

                titleSuggestions = emptyList(),

                accounts = accounts,
                categories = categories
            )
        }

    private suspend fun loadRequiredData() =
        accountsAct thenInvokeAfter { accs ->
            Pair(accs, categoriesAct(Unit))
        }

    private suspend fun createNewTransaction(state: TrnState.NewTransaction) = with(state) {
        if (amount <= BigDecimal.ZERO) {
            return@with Res.Err("Transaction's amount can NOT be zero. Must be >0!")
        }

        if (type == TransactionType.TRANSFER && toAccount == null) {
            //TRANSFER w/o toAccount
            return@with Res.Err("Transfers must have \"To\" account.")
        }

        Res.Ok(
            Transaction(
                id = UUID.randomUUID(),
                amount = amount,
                accountId = account.id,
                type = type,
                categoryId = category?.id,
                title = title,
                description = description,
                toAccountId = toAccount?.id,
                toAmount = toAmount ?: amount, //TODO: Handle properly transfers exchange rate
                dateTime = (date as? TrnDate.ActualDate)?.dateTime,
                dueDate = (date as? TrnDate.DueDate)?.dueDate?.atTime(12, 0),

                attachmentUrl = null,

                isDeleted = false,
                isSynced = false,
            )
        )
    }

    private fun isEditMode(): Boolean = stateVal() is TrnState.EditTransaction
}