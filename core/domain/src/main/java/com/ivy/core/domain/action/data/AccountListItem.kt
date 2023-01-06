package com.ivy.core.domain.action.data

import com.ivy.data.account.Account
import com.ivy.data.account.Folder

sealed interface AccountListItem {
    data class AccountHolder(val account: Account) : AccountListItem
    data class FolderWithAccounts(
        val folder: Folder,
        val accounts: List<Account>,
    ) : AccountListItem

    data class Archived(val accounts: List<Account>) : AccountListItem
}