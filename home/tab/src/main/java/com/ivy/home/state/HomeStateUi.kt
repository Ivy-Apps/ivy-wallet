package com.ivy.home.state

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.transaction.TransactionsListUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class HomeStateUi(
    val period: SelectedPeriodUi?,
    val trnsList: TransactionsListUi,
    val balance: ValueUi,
    val income: ValueUi,
    val expense: ValueUi,
    val hideBalance: Boolean,
    val moreMenuVisible: Boolean,

    val addTransactionModal: IvyModal,
)