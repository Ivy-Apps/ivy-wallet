package com.ivy.transaction.create.trn

import com.ivy.common.isNotNullOrBlank
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.ui.action.ExchangeInBaseCurrencyFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.trn.MapTrnTimeUiAct
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.transaction.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.navigation.Navigator
import com.ivy.transaction.action.TitleSuggestionsFlow
import com.ivy.transaction.create.CreateTrnController
import com.ivy.transaction.create.action.PreselectedAccountAct
import com.ivy.transaction.create.action.WriteLastUsedAccount
import com.ivy.transaction.create.data.CreateTrnStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    private val timeProvider: TimeProvider,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    private val navigator: Navigator,
    private val writeTrnsAct: WriteTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val preselectedAccountAct: PreselectedAccountAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val writeLastUsedAccount: WriteLastUsedAccount,
    private val exchangeInBaseCurrencyFlow: ExchangeInBaseCurrencyFlow,
    private val titleSuggestionsFlow: TitleSuggestionsFlow,
    private val createTrnController: CreateTrnController,
) : SimpleFlowViewModel<NewTrnState, NewTrnEvent>() {

    private val trnTypeModal = IvyModal()

    override val initialUi = NewTrnState(
        trnType = TransactionType.Expense,
        amount = CombinedValueUi.initial(),
        amountBaseCurrency = null,
        account = dummyAccountUi(),
        category = null,
        timeUi = TrnTimeUi.Actual("", ""),
        time = TrnTime.Actual(timeProvider.timeNow()),
        title = null,
        description = null,

        titleSuggestions = emptyList(),

        createFlow = createTrnController.uiFlow,
        trnTypeModal = trnTypeModal,
    )

    // region State
    private val trnType = MutableStateFlow(initialUi.trnType)
    private val amount = MutableStateFlow(initialUi.amount)
    private val account = MutableStateFlow(initialUi.account)
    private val category = MutableStateFlow(initialUi.category)
    private val time = MutableStateFlow<TrnTime>(TrnTime.Actual(timeProvider.timeNow()))
    private val timeUi = MutableStateFlow(initialUi.timeUi)
    private val title = MutableStateFlow(initialUi.title)
    private val description = MutableStateFlow(initialUi.description)
    // endregion

    override val uiFlow: Flow<NewTrnState> = combine(
        trnType, amountFlow(), accountCategoryFlow(), textsFlow(), timeFlow(),
    ) { trnType, (amount, amountBaseCurrency), (account, category),
        (title, description, titleSuggestions), (time, timeUi) ->
        NewTrnState(
            trnType = trnType,
            amount = amount,
            amountBaseCurrency = amountBaseCurrency,
            account = account,
            category = category,
            timeUi = timeUi,
            time = time,
            title = title,
            description = description,

            titleSuggestions = titleSuggestions,
            createFlow = createTrnController.uiFlow,
            trnTypeModal = trnTypeModal,
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

    private fun timeFlow() = combine(
        time, timeUi
    ) { time, timeUi ->
        time to timeUi
    }

    // region Event Handling
    override suspend fun handleEvent(event: NewTrnEvent) = when (event) {
        is NewTrnEvent.Initial -> handleInitial(event)
        is NewTrnEvent.AccountChange -> handleAccountChange(event)
        NewTrnEvent.Add -> handleAdd()
        is NewTrnEvent.AmountChange -> handleAmountChange(event)
        is NewTrnEvent.CategoryChange -> handleCategoryChange(event)
        NewTrnEvent.Close -> handleClose()
        is NewTrnEvent.TitleChange -> handleTitleChange(event)
        is NewTrnEvent.DescriptionChange -> handleDescriptionChange(event)
        is NewTrnEvent.TrnTimeChange -> handleTrnTimeChange(event)
        is NewTrnEvent.TrnTypeChange -> handleTrnTypeChange(event)
    }

    private suspend fun handleInitial(event: NewTrnEvent.Initial) {
        createTrnController.startFlow()

        val arg = event.arg
        trnType.value = arg.trnType
        category.value = arg.categoryId?.let {
            categoryByIdAct(it)
        }?.let {
            mapCategoryUiAct.invoke(it)
        }
        preselectedAccountAct(
            PreselectedAccountAct.Input(
                preselectedAccountId = arg.accountId
            )
        )?.let {
            account.value = it
        }
        timeUi.value = mapTrnTimeUiAct(time.value)
        amount.value = CombinedValueUi(
            amount = 0.0,
            currency = baseCurrencyAct(Unit),
            shortenFiat = false,
        )
    }

    private suspend fun handleAdd() {
        val account = accountByIdAct(account.value.id) ?: return
        val category = category.value?.id?.let { categoryByIdAct(it) }

        val transaction = Transaction(
            id = UUID.randomUUID(),
            account = account,
            category = category,
            type = trnType.value,
            value = amount.value.value,
            time = time.value,
            title = title.value,
            description = description.value,
            state = TrnState.Default,
            purpose = null,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            ),
            tags = emptyList(),
            attachments = emptyList(),
            metadata = TrnMetadata(
                recurringRuleId = null,
                loanId = null,
                loanRecordId = null,
            )
        )

        writeTrnsAct(
            WriteTrnsAct.Input.CreateNew(transaction)
        )
        closeScreen()
    }


    private fun handleClose() {
        closeScreen()
    }

    private fun closeScreen() {
        createTrnController.hideKeyboard()
        navigator.back()
    }

    // region Handle Value changes
    private fun handleAmountChange(event: NewTrnEvent.AmountChange) {
        amount.value = CombinedValueUi(
            value = event.amount,
            shortenFiat = false
        )

        createTrnController.nextStep(after = CreateTrnStep.Amount)
    }

    private suspend fun handleAccountChange(event: NewTrnEvent.AccountChange) {
        account.value = event.account
        writeLastUsedAccount(WriteLastUsedAccount.Input(event.account.id))
        changeAmountToAccountCurrency(event.account)

        createTrnController.nextStep(after = CreateTrnStep.Account)
    }

    private suspend fun changeAmountToAccountCurrency(
        account: AccountUi
    ) {
        accountByIdAct(account.id)?.let {
            val accountCurrency = it.currency
            amount.value = CombinedValueUi(
                value = amount.value.value.copy(currency = accountCurrency),
                shortenFiat = false
            )
        }
    }

    private fun handleCategoryChange(event: NewTrnEvent.CategoryChange) {
        category.value = event.category

        createTrnController.nextStep(after = CreateTrnStep.Category)
    }

    private fun handleTitleChange(event: NewTrnEvent.TitleChange) {
        title.value = event.title.takeIf { it.isNotBlank() }
    }

    private fun handleDescriptionChange(event: NewTrnEvent.DescriptionChange) {
        description.value = event.description.takeIf { it.isNotNullOrBlank() }

        createTrnController.nextStep(after = CreateTrnStep.Description)
    }

    private suspend fun handleTrnTimeChange(event: NewTrnEvent.TrnTimeChange) {
        time.value = event.time
        timeUi.value = mapTrnTimeUiAct(event.time)

        createTrnController.nextStep(after = CreateTrnStep.Date)
    }

    private fun handleTrnTypeChange(event: NewTrnEvent.TrnTypeChange) {
        trnType.value = event.trnType
    }
    // endregion
    // endregion
}