package com.ivy.core.ui.data.account

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AccountListItemUi {
    data class AccountHolder(val account: AccountUi) : AccountListItemUi
    data class FolderHolder(val folder: AccountFolderUi) : AccountListItemUi
}