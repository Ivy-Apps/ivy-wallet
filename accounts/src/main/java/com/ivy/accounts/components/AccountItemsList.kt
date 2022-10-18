package com.ivy.accounts.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.accounts.data.AccountListItemUi.*
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.design.l1_buildingBlocks.SpacerVer

fun LazyListScope.accountItemsList(
    items: List<AccountListItemUi>,
    onAccountClick: (AccountUi) -> Unit,
    onFolderClick: (FolderUi) -> Unit,
) {
    items(
        items = items,
        key = {
            when (it) {
                is AccountWithBalance -> "acc_${it.account.id}"
                is FolderWithAccounts -> "folder_${it.folder.id}"
                is Archived -> "archived_accounts"
            }
        }
    ) { item ->
        when (item) {
            is AccountWithBalance -> {
                SpacerVer(height = 12.dp)
                AccountCard(
                    account = item.account,
                    balance = item.balance,
                    balanceBaseCurrency = item.balanceBaseCurrency,
                    onClick = { onAccountClick(item.account) }
                )
            }
            is FolderWithAccounts -> {
                SpacerVer(height = 12.dp)
                AccountFolderCard(
                    folder = item.folder,
                    balance = item.balance,
                    accounts = item.accItems,
                    accountsCount = item.accountsCount,
                    onAccountClick = onAccountClick,
                    onFolderClick = {
                        onFolderClick(item.folder)
                    },
                )
            }
            is Archived -> {
                SpacerVer(height = 16.dp)
                ArchivedAccounts(archived = item, onAccountClick = onAccountClick)
            }
        }
    }

    // TODO: Implement empty state
}