package com.ivy.home

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.algorithm.trnhistory.data.TrnListItemUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class HomeStateUi(
    val balance: ValueUi,
    val income: ValueUi,
    val expense: ValueUi,
    val trnListItems: List<TrnListItemUi>,

    val hideBalance: Boolean,
    val bottomBarVisible: Boolean,

    val addTransactionModal: IvyModal,
)