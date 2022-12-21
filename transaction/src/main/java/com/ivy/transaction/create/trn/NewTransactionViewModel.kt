package com.ivy.transaction.create.trn

import androidx.compose.ui.focus.FocusRequester
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l2_components.modal.IvyModal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(

) : SimpleFlowViewModel<NewTrnState, NewTrnEvent>() {
    private val titleFocus = FocusRequester()
    private val amountModal = IvyModal()
    private val categoryModal = IvyModal()
    private val descriptionModal = IvyModal()
    private val dateModal = IvyModal()
    private val trnTypeModal = IvyModal()

    override val initialUi = NewTrnState(
        trnType = TransactionType.Expense,
        amount = ValueUi(amount = "0.0", currency = ""),
        account = null,
        category = null,
        time = TrnTimeUi.Actual(""),
        titleFocus = titleFocus,
        amountModal = amountModal,
        categoryModal = categoryModal,
        descriptionModal = descriptionModal,
        dateModal = dateModal,
        trnTypeModal = trnTypeModal,
    )

    override val uiFlow: Flow<NewTrnState> = flowOf(initialUi)


    // region Event Handling
    override suspend fun handleEvent(event: NewTrnEvent) {
        // TODO
    }
    // endregion
}