package com.ivy.core.domain.action.data

import com.ivy.data.account.Account
import com.ivy.data.account.AccountFolder

sealed interface AccountListItem {
    data class AccountHolder(val account: Account) : AccountListItem
    data class FolderHolder(val folder: AccountFolder) : AccountListItem
}