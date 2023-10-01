package com.ivy.accounts

import com.ivy.legacy.datamodel.Account

sealed interface AccountsEvent {
    data class OnReorder(val reorderedList: List<com.ivy.legacy.data.model.AccountData>) :
        AccountsEvent

    data class OnEditAccount(val editedAccount: Account, val newBalance: Double) : AccountsEvent
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountsEvent
}