package com.ivy.accounts

import com.ivy.data.model.Account

sealed interface AccountsEvent {
    data class OnReorder(val reorderedList: List<com.ivy.legacy.data.model.AccountData>) :
        AccountsEvent
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountsEvent
    data class OnVisibilityUpdate(val updatedList: List<Account>) :
        AccountsEvent
    data class OnHideModalVisible(val hideVisible: Boolean) : AccountsEvent
}
