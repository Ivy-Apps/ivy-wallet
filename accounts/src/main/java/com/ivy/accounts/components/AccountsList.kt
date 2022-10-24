package com.ivy.accounts.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.R
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.accounts.data.AccountListItemUi.*
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

fun LazyListScope.accountsList(
    items: List<AccountListItemUi>,
    noAccounts: Boolean,
    onAccountClick: (AccountUi) -> Unit,
    onFolderClick: (FolderUi) -> Unit,
    onCreateAccount: () -> Unit,
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
                SpacerVer(height = 8.dp)
                AccountCard(
                    account = item.account,
                    balance = item.balance,
                    balanceBaseCurrency = item.balanceBaseCurrency,
                    onClick = { onAccountClick(item.account) }
                )
            }
            is FolderWithAccounts -> {
                SpacerVer(height = 8.dp)
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

    if (noAccounts) {
        item {
            EmptyState(onCreateAccount = onCreateAccount)
        }
    }
}

@Composable
private fun EmptyState(
    onCreateAccount: () -> Unit
) {
    SpacerVer(height = 96.dp)
    B1(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "No accounts",
        textAlign = TextAlign.Center,
        color = UI.colors.primary
    )
    SpacerVer(height = 12.dp)
    B2(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "To use Ivy Wallet you need to create an account first.",
        textAlign = TextAlign.Center
    )
    SpacerVer(height = 12.dp)
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Focused,
        feeling = Feeling.Positive,
        text = "Create account",
        icon = R.drawable.ic_vue_money_wallet,
        onClick = onCreateAccount,
    )
    SpacerVer(height = 24.dp)
}


// region Preview
@Preview
@Composable
private fun Preview_EmptyState() {
    ComponentPreview {
        LazyColumn {
            accountsList(
                items = emptyList(),
                noAccounts = true,
                onFolderClick = {},
                onCreateAccount = {},
                onAccountClick = {},
            )
        }
    }
}
// endregion