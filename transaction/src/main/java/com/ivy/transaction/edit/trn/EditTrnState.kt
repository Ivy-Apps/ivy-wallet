package com.ivy.transaction.edit.trn

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.design.util.KeyboardController

@Immutable
data class EditTrnState(
    val trnType: TransactionType,
    val amount: CombinedValueUi,
    val amountBaseCurrency: ValueUi?,
    val account: AccountUi,
    val category: CategoryUi?,
    val timeUi: TrnTimeUi,
    val time: TrnTime,
    val title: String?,
    val description: String?,
    val hidden: Boolean,

    val titleSuggestions: List<String>,

    val keyboardController: KeyboardController,
)