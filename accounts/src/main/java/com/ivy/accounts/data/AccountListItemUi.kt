package com.ivy.accounts.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi

@Immutable
sealed interface AccountListItemUi {
    @Immutable
    data class AccountWithBalance(
        val account: AccountUi,
        val balance: ValueUi,
        val balanceBaseCurrency: ValueUi?,
    ) : AccountListItemUi

    @Immutable
    data class FolderWithAccounts(
        val folder: FolderUi,
        val accItems: List<AccountWithBalance>,
        val accountsCount: Int,
        val balance: ValueUi,
    ) : AccountListItemUi

    @Immutable
    data class Archived(
        val accHolders: List<AccountWithBalance>,
        val accountsCount: Int,
    ) : AccountListItemUi
}