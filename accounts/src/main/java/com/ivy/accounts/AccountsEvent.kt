package com.ivy.accounts

import com.ivy.base.AccountData
import com.ivy.data.Account

sealed class AccountsEvent {
    data class OnReorder(val reorderedList: List<AccountData>) : AccountsEvent()
    data class OnEditAccount(val editedAccount: Account, val newBalance: Double) : AccountsEvent()
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountsEvent()
}