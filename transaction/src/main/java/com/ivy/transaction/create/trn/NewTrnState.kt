package com.ivy.transaction.create.trn

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.transaction.create.CreateTrnFlowUiState

@Immutable
data class NewTrnState(
    val trnType: TransactionType,
    val amount: CombinedValueUi,
    val amountBaseCurrency: ValueUi?,
    val account: AccountUi,
    val category: CategoryUi?,
    val timeUi: TrnTimeUi,
    val time: TrnTime,
    val title: String?,
    val description: String?,

    val titleSuggestions: List<String>,

    // region Create flow
    val createFlow: CreateTrnFlowUiState,
    val trnTypeModal: IvyModal,
    // endregion
)