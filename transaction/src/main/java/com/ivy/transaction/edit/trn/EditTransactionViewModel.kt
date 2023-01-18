package com.ivy.transaction.edit.trn

import com.ivy.common.isNotEmpty
import com.ivy.common.isNotNullOrBlank
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUIDOrNull
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.transaction.TrnByIdAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.ui.action.ExchangeInBaseCurrencyFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.action.mapping.trn.MapTrnTimeUiAct
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import com.ivy.design.util.KeyboardController
import com.ivy.navigation.Navigator
import com.ivy.transaction.action.TitleSuggestionsFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val timeProvider: TimeProvider,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    private val navigator: Navigator,
    private val writeTrnsAct: WriteTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val exchangeInBaseCurrencyFlow: ExchangeInBaseCurrencyFlow,
    private val titleSuggestionsFlow: TitleSuggestionsFlow,
    private val trnByIdAct: TrnByIdAct,
    private val mapAccountUiAct: MapAccountUiAct,
) : SimpleFlowViewModel<EditTrnState, EditTrnEvent>() {

    private val keyboardController = KeyboardController()

    override val initialUi = EditTrnState(
        trnType = TransactionType.Expense,
        amount = CombinedValueUi.initial(),
        amountBaseCurrency = null,
        account = dummyAccountUi(),
        category = null,
        timeUi = TrnTimeUi.Actual("", ""),
        time = TrnTime.Actual(timeProvider.timeNow()),
        title = null,
        description = null,
        hidden = false,

        titleSuggestions = emptyList(),
        keyboardController = keyboardController,
    )

    private var transaction: Transaction? = null

    // region State
    private val trnType = MutableStateFlow(initialUi.trnType)
    private val amount = MutableStateFlow(initialUi.amount)
    private val account = MutableStateFlow(initialUi.account)
    private val category = MutableStateFlow(initialUi.category)
    private val time = MutableStateFlow<TrnTime>(TrnTime.Actual(timeProvider.timeNow()))
    private val timeUi = MutableStateFlow(initialUi.timeUi)
    private val title = MutableStateFlow(initialUi.title)
    private val description = MutableStateFlow(initialUi.description)
    private val hidden = MutableStateFlow(false)
    // endregion

    override val uiFlow: Flow<EditTrnState> = combine(
        trnType, amountFlow(), accountCategoryFlow(), textsFlow(), othersFlow(),
    ) { trnType, (amount, amountBaseCurrency), (account, category),
        (title, description, titleSuggestions), (time, timeUi, hidden) ->
        EditTrnState(
            trnType = trnType,
            amount = amount,
            amountBaseCurrency = amountBaseCurrency,
            account = account,
            category = category,
            timeUi = timeUi,
            time = time,
            title = title,
            description = description,
            hidden = hidden,

            titleSuggestions = titleSuggestions,
            keyboardController = keyboardController,
        )
    }

    private fun amountFlow() = amount.map { amount ->
        exchangeInBaseCurrencyFlow(amount.value).map { amountBaseCurrency ->
            amount to amountBaseCurrency
        }
    }.flattenLatest()

    private fun textsFlow() = combine(
        title, description, category,
    ) { title, description, category ->
        titleSuggestionsFlow(
            TitleSuggestionsFlow.Input(
                title = title,
                categoryUi = category,
                transfer = false,
            )
        ).map { titleSuggestions ->
            Triple(title, description, titleSuggestions)
        }
    }.flattenLatest()

    private fun accountCategoryFlow() = combine(
        account, category
    ) { account, category ->
        account to category
    }

    private fun othersFlow() = combine(
        time, timeUi, hidden
    ) { time, timeUi, hidden ->
        Triple(time, timeUi, hidden)
    }


    // region Event Handling
    override suspend fun handleEvent(event: EditTrnEvent) = when (event) {
        is EditTrnEvent.Initial -> handleInitial(event)
        is EditTrnEvent.Save -> handleSave()
        is EditTrnEvent.Delete -> handleDelete()
        is EditTrnEvent.Close -> handleClose()
        is EditTrnEvent.AccountChange -> handleAccountChange(event)
        is EditTrnEvent.AmountChange -> handleAmountChange(event)
        is EditTrnEvent.CategoryChange -> handleCategoryChange(event)
        is EditTrnEvent.DescriptionChange -> handleDescriptionChange(event)
        is EditTrnEvent.TitleChange -> handleTitleChange(event)
        is EditTrnEvent.TrnTimeChange -> handleTrnTimeChange(event)
        is EditTrnEvent.TrnTypeChange -> handleTrnTypeChange(event)
        is EditTrnEvent.HiddenChange -> handleHiddenChange(event)
    }

    private suspend fun handleInitial(event: EditTrnEvent.Initial) {
        val transaction = event.trnId.toUUIDOrNull()?.let { trnId ->
            trnByIdAct(trnId)
        }

        if (transaction != null) {
            this.transaction = transaction
            trnType.value = transaction.type
            amount.value = CombinedValueUi(
                value = transaction.value,
                shortenFiat = false,
            )
            account.value = mapAccountUiAct(transaction.account)
            category.value = transaction.category?.let { category ->
                mapCategoryUiAct(category)
            }
            time.value = transaction.time
            timeUi.value = mapTrnTimeUiAct(transaction.time)
            title.value = transaction.title
            description.value = transaction.description.takeIf { it.isNotNullOrBlank() }
        } else {
            closeScreen()
        }
    }

    private suspend fun handleSave() {
        val original = transaction ?: return
        val account = accountByIdAct(account.value.id) ?: return
        val category = category.value?.id?.let { categoryByIdAct(it) }

        if (amount.value.value.amount <= 0.0) return

        val updated = original.copy(
            account = account,
            category = category,
            value = amount.value.value,
            time = time.value,
            title = title.value.takeIf { it.isNotNullOrBlank() },
            description = description.value.takeIf { it.isNotNullOrBlank() },
            type = trnType.value,
            state = if (hidden.value) TrnState.Hidden else TrnState.Default,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            ),
        )

        writeTrnsAct(
            WriteTrnsAct.Input.Update(
                old = original,
                new = updated,
            )
        )
        closeScreen()
    }

    private suspend fun handleDelete() {
        val original = transaction ?: return
        writeTrnsAct(
            WriteTrnsAct.Input.Delete(
                trnId = original.id.toString(),
                affectedAccountIds = setOf(original.account.id.toString(), account.value.id),
                originalTime = original.time,
            )
        )
        closeScreen()
    }

    private fun handleClose() {
        closeScreen()
    }

    private fun closeScreen() {
        keyboardController.hide()
        navigator.back()
    }

    // region Handle value changes
    private fun handleAccountChange(event: EditTrnEvent.AccountChange) {
        account.value = event.account
    }

    private fun handleCategoryChange(event: EditTrnEvent.CategoryChange) {
        category.value = event.category
    }

    private fun handleAmountChange(event: EditTrnEvent.AmountChange) {
        amount.value = CombinedValueUi(
            value = event.amount,
            shortenFiat = false
        )
    }

    private fun handleTitleChange(event: EditTrnEvent.TitleChange) {
        title.value = event.title.takeIf { it.isNotEmpty() }
    }

    private fun handleDescriptionChange(event: EditTrnEvent.DescriptionChange) {
        description.value = event.description.takeIf { it.isNotEmpty() }
    }

    private suspend fun handleTrnTimeChange(event: EditTrnEvent.TrnTimeChange) {
        time.value = event.time
        timeUi.value = mapTrnTimeUiAct(event.time)
    }

    private fun handleTrnTypeChange(event: EditTrnEvent.TrnTypeChange) {
        trnType.value = event.trnType
    }

    private fun handleHiddenChange(event: EditTrnEvent.HiddenChange) {
        hidden.value = event.hidden
    }
    // endregion
    // endregion
}