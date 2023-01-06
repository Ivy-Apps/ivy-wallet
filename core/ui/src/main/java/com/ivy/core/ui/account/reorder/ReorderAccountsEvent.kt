package com.ivy.core.ui.account.reorder

import com.ivy.core.ui.account.reorder.data.ReorderAccListItemUi

sealed interface ReorderAccountsEvent {
    data class Reorder(
        val reordered: List<ReorderAccListItemUi>
    ) : ReorderAccountsEvent
}