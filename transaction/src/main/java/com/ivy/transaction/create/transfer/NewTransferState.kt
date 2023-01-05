package com.ivy.transaction.create.transfer

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.transaction.create.CreateTrnFlowUiState
import com.ivy.transaction.data.TransferRateUi

@Immutable
data class NewTransferState(
    val accountFrom: AccountUi,
    val accountTo: AccountUi,
    val amountFrom: CombinedValueUi,
    val amountTo: CombinedValueUi,

    val category: CategoryUi?,
    val timeUi: TrnTimeUi,
    val time: TrnTime,
    val title: String?,
    val description: String?,
    val fee: CombinedValueUi,
    val rate: TransferRateUi?,

    val titleSuggestions: List<String>,
    val createFlow: CreateTrnFlowUiState,
    val feeModal: IvyModal,
    val rateModal: IvyModal,
)