package com.ivy.accounts.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.core.ui.data.account.AccountUi

@Immutable
sealed interface AccountListItemUi {
    @Immutable
    data class AccountHolder(
        val account: AccountUi,
        val balance: ValueUi,
        val balanceBaseCurrency: ValueUi?,
    ) : AccountListItemUi

    @Immutable
    data class FolderHolder(
        val folder: AccountFolderUi,
        val accItems: List<AccountHolder>,
        val balance: ValueUi,
    ) : AccountListItemUi

    @Immutable
    data class Archived(
        val accHolders: List<AccountHolder>,
        val accountsCount: Int,
    ) : AccountListItemUi
}