package com.ivy.wallet.ui.accounts

import com.ivy.wallet.domain.data.core.Account

sealed class AccountsEvent {
    data class OnReorder(val reorderedList: List<AccountData>) : AccountsEvent()
    data class OnEditAccount(val editedAccount: Account, val newBalance: Double) : AccountsEvent()
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountsEvent()
}