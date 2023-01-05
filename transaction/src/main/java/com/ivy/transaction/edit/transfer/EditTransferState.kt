package com.ivy.transaction.edit.transfer

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TrnTime
import com.ivy.design.util.KeyboardController
import com.ivy.transaction.data.TransferRateUi

@Immutable
data class EditTransferState(
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

    val keyboardController: KeyboardController,
)