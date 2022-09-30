package com.ivy.accounts

import com.ivy.base.AccountData
import com.ivy.data.AccountOld

sealed interface AccountEvent {
    data class OnReorder(val reorderedList: List<AccountData>) : AccountEvent
    data class OnEditAccount(val editedAccount: AccountOld, val newBalance: Double) : AccountEvent
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountEvent

    data class BottomBarAction(val action: AccBottomBarAction) : AccountEvent
}