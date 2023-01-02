package com.ivy.transaction.create.transfer

import androidx.compose.ui.focus.FocusRequester
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.account.AccountsAct
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.util.combine
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.ui.action.BaseCurrencyRepresentationFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.MapTrnTimeUiAct
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.util.KeyboardController
import com.ivy.navigation.Navigator
import com.ivy.transaction.action.TitleSuggestionsFlow
import com.ivy.transaction.create.action.CreateTrnFlowAct
import com.ivy.transaction.create.action.WriteLastUsedAccount
import com.ivy.transaction.create.data.CreateTrnFlow
import com.ivy.transaction.create.data.CreateTrnFlowStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NewTransferViewModel @Inject constructor(
    timeProvider: TimeProvider,
    private val titleSuggestionsFlow: TitleSuggestionsFlow,
    private val createTrnFlowAct: CreateTrnFlowAct,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    private val navigator: Navigator,
    private val writeTrnsAct: WriteTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val writeLastUsedAccount: WriteLastUsedAccount,
    private val baseCurrencyRepresentationFlow: BaseCurrencyRepresentationFlow,
    private val accountsAct: AccountsAct,
    private val mapAccountUiAct: MapAccountUiAct,
) : SimpleFlowViewModel<NewTransferState, NewTransferEvent>() {
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

    override val initialUi = NewTransferState(
        accountFrom = dummyAccountUi(),
        accountTo = dummyAccountUi(),
        amountFrom = CombinedValueUi.initial(),
        amountTo = CombinedValueUi.initial(),
        category = null,
        timeUi = TrnTimeUi.Actual(""),
        time = TrnTime.Actual(timeProvider.timeNow()),
        title = null,
        description = null,
        fee = null,

        titleSuggestions = emptyList(),

        titleFocus = titleFocus,
        keyboardController = keyboardController,
        amountModal = amountModal,
        categoryPickerModal = categoryPickerModal,
        accountPickerModal = accountPickerModal,
        descriptionModal = descriptionModal,
        timeModal = trnTimeModal,
    )

    // region State
    private val amountFrom = MutableStateFlow(initialUi.amountFrom)
    private val amountTo = MutableStateFlow(initialUi.amountTo)
    private val accountFrom = MutableStateFlow(initialUi.accountFrom)
    private val accountTo = MutableStateFlow(initialUi.accountTo)
    private val category = MutableStateFlow(initialUi.category)
    private val time = MutableStateFlow<TrnTime>(TrnTime.Actual(timeProvider.timeNow()))
    private val timeUi = MutableStateFlow(initialUi.timeUi)
    private val title = MutableStateFlow(initialUi.title)
    private val description = MutableStateFlow(initialUi.description)
    private val fee = MutableStateFlow(initialUi.fee)
    // endregion


    override val uiFlow = combine(
        amountFrom, amountTo,
        accountFrom, accountTo, category, time, timeUi,
        title, description, fee,
    )
    { amountFrom, amountTo,
      accountFrom, accountTo, category, time, timeUi,
      title, description, fee ->
        titleSuggestionsFlow(
            TitleSuggestionsFlow.Input(
                title = title,
                categoryUi = category,
                transfer = true,
            )
        ).map { titleSuggestions ->
            NewTransferState(
                amountFrom = amountFrom,
                amountTo = amountTo,
                accountFrom = accountFrom,
                accountTo = accountTo,
                category = category,
                time = time,
                timeUi = timeUi,
                title = title,
                description = description,
                fee = fee,

                titleSuggestions = titleSuggestions,

                titleFocus = titleFocus,
                keyboardController = keyboardController,
                amountModal = amountModal,
                categoryPickerModal = categoryPickerModal,
                accountPickerModal = accountPickerModal,
                descriptionModal = descriptionModal,
                timeModal = trnTimeModal,
            )
        }
    }.flattenLatest()


    // region Event Handling
    override suspend fun handleEvent(event: NewTransferEvent) = when (event) {
        NewTransferEvent.Initial -> handleInitial()
        NewTransferEvent.Close -> handleClose()
        NewTransferEvent.Add -> handleAdd()
        is NewTransferEvent.CategoryChange -> TODO()
        is NewTransferEvent.DescriptionChange -> TODO()
        is NewTransferEvent.FromAccountChange -> TODO()
        is NewTransferEvent.FromAmountChange -> TODO()
        is NewTransferEvent.TitleChange -> TODO()
        is NewTransferEvent.ToAccountChange -> TODO()
        is NewTransferEvent.ToAmountChange -> TODO()
        is NewTransferEvent.TrnTimeChange -> TODO()
    }

    private suspend fun handleInitial() {
        val createTrnFlow = createTrnFlowAct(Unit).also {
            createTrnFlow = it
        }
        flowStep(createTrnFlow.first).execute()

        val accounts = accountsAct(Unit)
        if (accounts.size <= 2) {
            // cannot do transfers with less than 2 accounts
            closeScreen()
            return
        }
        val fromAcc = accounts.first()
        val toAcc = accounts[1] // 2nd

        accountFrom.value = mapAccountUiAct(fromAcc)
        accountTo.value = mapAccountUiAct(toAcc)

        amountFrom.value = CombinedValueUi(
            amount = 0.0,
            currency = fromAcc.currency,
            shortenFiat = false,
        )
        amountTo.value = CombinedValueUi(
            amount = 0.0,
            currency = toAcc.currency,
            shortenFiat = false,
        )

        timeUi.value = mapTrnTimeUiAct(time.value)
    }

    private fun handleAdd() {

    }

    private fun handleClose() {
        closeScreen()
    }

    private fun closeScreen() {
        keyboardController.hide()
        navigator.back()
    }
    // endregion
}