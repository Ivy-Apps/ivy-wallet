package com.ivy.transaction.edit.transfer

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountByIdAct
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.exchange.ExchangeAct
import com.ivy.core.domain.action.transaction.transfer.ModifyTransfer
import com.ivy.core.domain.action.transaction.transfer.TransferByBatchIdAct
import com.ivy.core.domain.action.transaction.transfer.TransferData
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.util.combine
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.domain.pure.util.takeIfNotBlank
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.action.mapping.trn.MapTrnTimeUiAct
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.transaction.Transfer
import com.ivy.data.transaction.TrnTime
import com.ivy.design.util.KeyboardController
import com.ivy.navigation.Navigator
import com.ivy.transaction.action.TitleSuggestionsFlow
import com.ivy.transaction.data.TransferRateUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class EditTransferViewModel @Inject constructor(
    private val timeProvider: TimeProvider,
    private val titleSuggestionsFlow: TitleSuggestionsFlow,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    private val navigator: Navigator,
    private val accountByIdAct: AccountByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val mapAccountUiAct: MapAccountUiAct,
    private val writeTransferAct: WriteTransferAct,
    private val exchangeAct: ExchangeAct,
    private val transferByBatchIdAct: TransferByBatchIdAct,
) : SimpleFlowViewModel<EditTransferState, EditTransferEvent>() {

    private val keyboardController = KeyboardController()

    override val initialUi = EditTransferState(
        accountFrom = dummyAccountUi(),
        accountTo = dummyAccountUi(),
        amountFrom = CombinedValueUi.initial(),
        amountTo = CombinedValueUi.initial(),
        category = null,
        timeUi = TrnTimeUi.Actual("", ""),
        time = TrnTime.Actual(timeProvider.timeNow()),
        title = null,
        description = null,
        fee = CombinedValueUi.initial(),
        rate = null,

        titleSuggestions = emptyList(),

        keyboardController = keyboardController,
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

    private lateinit var transfer: Transfer

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
            EditTransferState(
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
                rate = if (amountFrom.value.currency != amountTo.value.currency &&
                    amountFrom.value.amount > 0.0 && amountTo.value.amount > 0.0
                ) {
                    // e.g. 1 EUR to 1.96 BGN
                    // => EUR-BGN = 1.96 / 1 = 1.96
                    val rateValue = amountTo.value.amount / amountFrom.value.amount
                    TransferRateUi(
                        rateValueFormatted = DecimalFormat(
                            "###,###,##0.${"#".repeat(6)}"
                        ).format(rateValue),
                        rateValue = rateValue,
                        fromCurrency = amountFrom.value.currency,
                        toCurrency = amountTo.value.currency,
                    )
                } else null,

                titleSuggestions = titleSuggestions,

                keyboardController = keyboardController,
            )
        }
    }.flattenLatest()


    // region Event Handling
    override suspend fun handleEvent(event: EditTransferEvent) = when (event) {
        is EditTransferEvent.Initial -> handleInitial(event)
        EditTransferEvent.Save -> handleSave()
        EditTransferEvent.Delete -> handleDelete()
        EditTransferEvent.Close -> handleClose()
        is EditTransferEvent.ToAmountChange -> handleToAmountChange(event)
        is EditTransferEvent.FromAmountChange -> handleFromAmountChange(event)
        is EditTransferEvent.FromAccountChange -> handleFromAccountChange(event)
        is EditTransferEvent.ToAccountChange -> handleToAccountChange(event)
        is EditTransferEvent.FeeChange -> handleFeeChange(event)
        is EditTransferEvent.FeePercent -> handleFeePercent(event)
        is EditTransferEvent.TitleChange -> handleTitleChange(event)
        is EditTransferEvent.DescriptionChange -> handleDescriptionChange(event)
        is EditTransferEvent.CategoryChange -> handleCategoryChange(event)
        is EditTransferEvent.TrnTimeChange -> handleTimeChange(event)
        is EditTransferEvent.RateChange -> handleRateChange(event)
    }

    private suspend fun handleInitial(event: EditTransferEvent.Initial) {
        val transfer = transferByBatchIdAct(event.batchId)?.also {
            this.transfer = it
        }

        if (transfer == null) {
            closeScreen()
            return
        }

        // Init UI state
        amountFrom.value = CombinedValueUi(
            value = transfer.from.value,
            shortenFiat = false,
        )
        accountFrom.value = mapAccountUiAct(transfer.from.account)

        amountTo.value = CombinedValueUi(
            value = transfer.to.value,
            shortenFiat = false,
        )
        accountTo.value = mapAccountUiAct(transfer.to.account)

        category.value = transfer.from.category?.let { mapCategoryUiAct(it) }
        time.value = transfer.time
        timeUi.value = mapTrnTimeUiAct(transfer.time)
        title.value = transfer.from.title
        description.value = transfer.from.description
        fee.value = transfer.fee?.value?.let {
            CombinedValueUi(
                value = it,
                shortenFiat = false,
            )
        } ?: CombinedValueUi(
            amount = 0.0, // no fee
            currency = transfer.from.value.currency,
            shortenFiat = false,
        )
    }

    private suspend fun handleSave() {
        val accountFrom = accountByIdAct(accountFrom.value.id) ?: return
        val accountTo = accountByIdAct(accountTo.value.id) ?: return
        val category = category.value?.let { categoryByIdAct(it.id) }

        val data = TransferData(
            accountFrom = accountFrom,
            accountTo = accountTo,
            amountFrom = amountFrom.value.value,
            amountTo = amountTo.value.value,
            category = category,
            time = time.value,
            title = title.value,
            description = description.value,
            fee = fee.value.value.takeIf { it.amount > 0.0 },
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )

        writeTransferAct(
            ModifyTransfer.edit(
                batchId = transfer.batchId,
                data = data
            )
        )

        closeScreen()
    }

    private suspend fun handleDelete() {
        writeTransferAct(ModifyTransfer.delete(transfer = transfer))
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

    private suspend fun handleFromAmountChange(event: EditTransferEvent.FromAmountChange) {
        updateFromAmount(event.amount)
    }

    private suspend fun updateFromAmount(
        newFromAmount: Value
    ) {
        val toAccount = accountByIdAct(accountTo.value.id) ?: return

        amountFrom.value = CombinedValueUi(
            value = newFromAmount,
            shortenFiat = false,
        )

        val rate = uiState.value.rate
        if (rate != null && rate.rateValue > 0) {
            // Custom exchange rate set by the user, use it
            amountTo.value = CombinedValueUi(
                amount = newFromAmount.amount * rate.rateValue,
                currency = toAccount.currency,
                shortenFiat = false,
            )
        } else {
            // No rate, exchange by latest rate
            amountTo.value = CombinedValueUi(
                value = exchangeAct(
                    ExchangeAct.Input(
                        value = newFromAmount,
                        outputCurrency = toAccount.currency
                    )
                ),
                shortenFiat = false,
            )
        }
    }

    private fun handleToAmountChange(event: EditTransferEvent.ToAmountChange) {
        amountTo.value = CombinedValueUi(
            value = event.amount,
            shortenFiat = false,
        )
    }

    private suspend fun handleFromAccountChange(event: EditTransferEvent.FromAccountChange) {
        accountFrom.value = event.account

        accountByIdAct(event.account.id)?.let {
            amountFrom.value = CombinedValueUi(
                amount = amountFrom.value.value.amount,
                currency = it.currency,
                shortenFiat = false,
            )
            fee.value = CombinedValueUi(
                amount = fee.value.value.amount,
                currency = it.currency,
                shortenFiat = false,
            )
        }
    }

    private suspend fun handleToAccountChange(event: EditTransferEvent.ToAccountChange) {
        accountTo.value = event.account

        accountByIdAct(event.account.id)?.let {
            amountTo.value = CombinedValueUi(
                amount = amountTo.value.value.amount,
                currency = it.currency,
                shortenFiat = false,
            )
        }
    }

    private fun handleFeeChange(event: EditTransferEvent.FeeChange) {
        fee.value = if (event.value != null) CombinedValueUi(
            value = event.value,
            shortenFiat = false,
        ) else {
            // no fee (0 fee)
            CombinedValueUi(
                amount = 0.0,
                currency = fee.value.value.currency,
                shortenFiat = false,
            )
        }
    }

    private fun handleFeePercent(event: EditTransferEvent.FeePercent) {
        fee.value = CombinedValueUi(
            amount = amountFrom.value.value.amount * event.percent,
            currency = fee.value.value.currency,
            shortenFiat = false,
        )
    }

    private fun handleRateChange(event: EditTransferEvent.RateChange) {
        amountTo.value = CombinedValueUi(
            amount = amountFrom.value.value.amount * event.newRate,
            currency = amountTo.value.value.currency,
            shortenFiat = false,
        )
    }

    private fun handleTitleChange(event: EditTransferEvent.TitleChange) {
        title.value = event.title.takeIfNotBlank()
    }

    private fun handleDescriptionChange(event: EditTransferEvent.DescriptionChange) {
        description.value = event.description.takeIfNotBlank()
    }

    private fun handleCategoryChange(event: EditTransferEvent.CategoryChange) {
        category.value = event.category
    }

    private suspend fun handleTimeChange(event: EditTransferEvent.TrnTimeChange) {
        time.value = event.time
        timeUi.value = mapTrnTimeUiAct(event.time)
    }
    // endregion
    // endregion
}