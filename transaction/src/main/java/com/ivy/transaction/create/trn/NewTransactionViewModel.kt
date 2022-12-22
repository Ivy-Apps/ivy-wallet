package com.ivy.transaction.create.trn

import androidx.compose.ui.focus.FocusRequester
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.MapTrnTimeUiAct
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.transaction.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.util.KeyboardController
import com.ivy.navigation.Navigator
import com.ivy.transaction.create.action.CreateTrnFlowAct
import com.ivy.transaction.create.action.PreselectedAccountAct
import com.ivy.transaction.create.data.CreateTrnFlow
import com.ivy.transaction.create.data.CreateTrnFlowStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    private val createTrnFlowAct: CreateTrnFlowAct,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    private val navigator: Navigator,
    private val writeTrnsAct: WriteTrnsAct,
    private val timeProvider: TimeProvider,
    private val accountByIdAct: AccountByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val preselectedAccountAct: PreselectedAccountAct,
    private val baseCurrencyAct: BaseCurrencyAct,
) : SimpleFlowViewModel<NewTrnState, NewTrnEvent>() {
    // region UX flow
    private interface FlowStep {
        fun execute()
    }

    class ModalStep(private val modal: IvyModal) : FlowStep {
        override fun execute() {
            modal.show()
        }
    }

    private val keyboardController = KeyboardController()
    private val titleFocus = FocusRequester()
    private val titleStep = object : FlowStep {
        override fun execute() {
            titleFocus.requestFocus()
            keyboardController.show()
        }
    }

    private val amountModal = IvyModal()
    private val amountStep = ModalStep(amountModal)

    private val categoryPickerModal = IvyModal()
    private val categoryStep = ModalStep(categoryPickerModal)

    private val accountPickerModal = IvyModal()
    private val accountStep = ModalStep(accountPickerModal)

    private val descriptionModal = IvyModal()
    private val descriptionStep = ModalStep(descriptionModal)

    private val trnTimeModal = IvyModal()
    private val timeStep = ModalStep(trnTimeModal)

    private val trnTypeModal = IvyModal()
    private val typeStep = ModalStep(trnTypeModal)

    private var createTrnFlow: CreateTrnFlow? = null
    private fun flowStep(step: CreateTrnFlowStep): FlowStep = when (step) {
        CreateTrnFlowStep.Title -> titleStep
        CreateTrnFlowStep.Amount -> amountStep
        CreateTrnFlowStep.Category -> categoryStep
        CreateTrnFlowStep.Account -> accountStep
        CreateTrnFlowStep.Description -> descriptionStep
        CreateTrnFlowStep.Time -> timeStep
        CreateTrnFlowStep.Type -> typeStep
    }

    private fun executeNextStep(after: CreateTrnFlowStep) {
        createTrnFlow?.steps?.get(after)?.let(::flowStep)?.execute()
    }
    // endregion

    override val initialUi = NewTrnState(
        trnType = TransactionType.Expense,
        amountUi = ValueUi(amount = "0.0", currency = ""),
        amount = Value(amount = 0.0, currency = ""),
        account = dummyAccountUi(),
        category = null,
        time = TrnTimeUi.Actual(""),
        title = null,
        description = null,

        titleFocus = titleFocus,
        keyboardController = keyboardController,
        amountModal = amountModal,
        categoryPickerModal = categoryPickerModal,
        accountPickerModal = accountPickerModal,
        descriptionModal = descriptionModal,
        trnTimeModal = trnTimeModal,
        trnTypeModal = trnTypeModal,
    )

    // region State
    private val trnType = MutableStateFlow(initialUi.trnType)
    private val amountUi = MutableStateFlow(initialUi.amountUi)
    private val amount = MutableStateFlow(initialUi.amount)
    private val account = MutableStateFlow(initialUi.account)
    private val category = MutableStateFlow(initialUi.category)
    private val time = MutableStateFlow<TrnTime>(TrnTime.Actual(timeProvider.timeNow()))
    private val timeUi = MutableStateFlow(initialUi.time)
    private val title = MutableStateFlow(initialUi.title)
    private val description = MutableStateFlow(initialUi.description)
    // endregion

    override val uiFlow: Flow<NewTrnState> = combine(
        trnType, amountFlow(), accountCategory(), textFlow(), timeUi,
    ) { trnType, (amount, amountUi), (account, category), (title, description), time ->
        NewTrnState(
            trnType = trnType,
            amount = amount,
            amountUi = amountUi,
            account = account,
            category = category,
            time = time,
            title = title,
            description = description,

            titleFocus = titleFocus,
            keyboardController = keyboardController,
            amountModal = amountModal,
            categoryPickerModal = categoryPickerModal,
            accountPickerModal = accountPickerModal,
            descriptionModal = descriptionModal,
            trnTimeModal = trnTimeModal,
            trnTypeModal = trnTypeModal,
        )
    }

    private fun amountFlow() = combine(
        amount, amountUi
    ) { amount, amountUi ->
        amount to amountUi
    }

    private fun textFlow() = combine(
        title, description
    ) { title, description ->
        title to description
    }

    private fun accountCategory() = combine(
        account, category
    ) { account, category ->
        account to category
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
        val createTrnFlow = createTrnFlowAct(Unit).also {
            createTrnFlow = it
        }
        flowStep(createTrnFlow.first).execute()

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
        amount.value = Value(amount = 0.0, currency = baseCurrencyAct(Unit))
        amountUi.value = format(amount.value, shortenFiat = false)
    }

    private suspend fun handleAdd() {
        val account = accountByIdAct(account.value.id) ?: return
        val category = category.value?.id?.let { categoryByIdAct(it) }

        val transaction = Transaction(
            id = UUID.randomUUID(),
            account = account,
            category = category,
            type = trnType.value,
            value = amount.value,
            time = time.value,
            title = title.value,
            description = description.value,
            state = TrnState.Default,
            purpose = null,
            sync = SyncState.Syncing,
            tags = emptyList(),
            attachments = emptyList(),
            metadata = TrnMetadata(
                recurringRuleId = null,
                loanId = null,
                loanRecordId = null,
            )
        )

        writeTrnsAct(Modify.save(transaction))

        navigator.back()
    }

    private fun handleAmountChange(event: NewTrnEvent.AmountChange) {
        amount.value = event.amount
        amountUi.value = format(event.amount, shortenFiat = false)

        executeNextStep(after = CreateTrnFlowStep.Amount)
    }

    private fun handleAccountChange(event: NewTrnEvent.AccountChange) {
        account.value = event.account

        executeNextStep(after = CreateTrnFlowStep.Account)

        // TODO: Maybe change amount to match account currency?
    }

    private fun handleCategoryChange(event: NewTrnEvent.CategoryChange) {
        category.value = event.category

        executeNextStep(after = CreateTrnFlowStep.Category)
    }

    private fun handleTitleChange(event: NewTrnEvent.TitleChange) {
        title.value = event.title.takeIf { it.isNotBlank() }
    }

    private fun handleDescriptionChange(event: NewTrnEvent.DescriptionChange) {
        description.value = event.description.takeIf { !it.isNullOrBlank() }

        executeNextStep(after = CreateTrnFlowStep.Description)
    }

    private suspend fun handleTrnTimeChange(event: NewTrnEvent.TrnTimeChange) {
        time.value = event.time
        timeUi.value = mapTrnTimeUiAct(event.time)

        executeNextStep(after = CreateTrnFlowStep.Time)
    }

    private fun handleTrnTypeChange(event: NewTrnEvent.TrnTypeChange) {
        trnType.value = event.trnType

        executeNextStep(after = CreateTrnFlowStep.Type)
    }

    private fun handleClose() {
        navigator.back()
    }
    // endregion
}