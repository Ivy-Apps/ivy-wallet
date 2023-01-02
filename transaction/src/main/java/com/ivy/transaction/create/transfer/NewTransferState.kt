package com.ivy.transaction.create.transfer

import androidx.compose.runtime.Immutable
import androidx.compose.ui.focus.FocusRequester
import com.ivy.core.domain.pure.format.CombinedValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.util.KeyboardController

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
    val fee: CombinedValueUi?,

    val titleSuggestions: List<String>,

    // region Create flow
    val titleFocus: FocusRequester,
    val keyboardController: KeyboardController,
    val transferAmountModal: IvyModal,
    val categoryPickerModal: IvyModal,
    val accountPickerModal: IvyModal,
    val descriptionModal: IvyModal,
    val timeModal: IvyModal,
    val feeModal: IvyModal,
    // endregion
)