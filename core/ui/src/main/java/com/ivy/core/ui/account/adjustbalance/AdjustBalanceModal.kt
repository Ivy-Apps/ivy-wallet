package com.ivy.core.ui.account.adjustbalance

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l2_components.modal.IvyModal

@Composable
fun BoxScope.AdjustBalanceModal(
    modal: IvyModal,
    balance: Value,
    level: Int = 1,
) {

    AmountModal(
        modal = modal,
        level = level,
        contentAbove = {

        },
        initialAmount = balance,
        onAmountEnter = {
            // TODO: Adjust balance
        }
    )
}