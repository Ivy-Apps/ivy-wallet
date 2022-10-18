package com.ivy.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.components.accountItemsList
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.accounts.modal.CreateModal
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.account.edit.EditAccountModal
import com.ivy.core.ui.account.folder.create.CreateAccFolderModal
import com.ivy.core.ui.account.folder.edit.EditAccFolderModal
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.account.dummyFolderUi
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.core.ui.value.AmountCurrencySmall
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.AccountTab() {
    val viewModel: AccountTabViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value
        ?: previewState()

    UI(state = state, onEvent = { viewModel?.onEvent(it) })
}

@Composable
private fun BoxScope.UI(
    state: AccountTabState,
    onEvent: (AccountTabEvent) -> Unit,
) {
    val editAccountModal = rememberIvyModal()
    var editAccountId by remember { mutableStateOf<String?>(null) }
    val editFolderModal = rememberIvyModal()
    var editFolderId by remember { mutableStateOf<String?>(null) }

    val reorderModal = rememberIvyModal()
    val createAccountModal = rememberIvyModal()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "header") {
            SpacerVer(height = 16.dp)
            Header(
                totalBalance = state.totalBalance,
                excludedBalance = state.excludedBalance,
                onReorder = {
                    reorderModal.show()
                }
            )
            SpacerVer(height = 16.dp)
        }
        accountItemsList(
            items = state.items,
            noAccounts = state.noAccounts,
            onAccountClick = {
                editAccountId = it.id
                editAccountModal.show()
            },
            onFolderClick = {
                editFolderId = it.id
                editFolderModal.show()
            },
            onCreateAccount = {
                createAccountModal.show()
            }
        )
        item {
            SpacerVer(height = 300.dp) // last item spacer
        }
    }

    val createFolderModal = rememberIvyModal()
    CreateModal(
        modal = state.createModal,
        onCreateAccount = { createAccountModal.show() },
        onCreateFolder = { createFolderModal.show() }
    )
    CreateAccountModal(modal = createAccountModal)
    CreateAccFolderModal(modal = createFolderModal)

    editAccountId?.let {
        EditAccountModal(modal = editAccountModal, accountId = it)
    }
    editFolderId?.let {
        EditAccFolderModal(modal = editFolderModal, folderId = it)
    }
}

@Composable
private fun Header(
    totalBalance: ValueUi,
    excludedBalance: ValueUi?,
    onReorder: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            B1(text = "Net-worth")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AmountCurrency(totalBalance, color = UI.colors.primary)
            }
        }
        SpacerHor(width = 4.dp)
        IvyButton(
            size = ButtonSize.Small,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = null,
            icon = R.drawable.round_reorder_24,
            onClick = onReorder,
        )
    }

    if (excludedBalance != null) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            B2(text = "Excluded:")
            SpacerHor(width = 8.dp)
            AmountCurrencySmall(excludedBalance, color = UI.colors.red)
        }
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        AccountTab()
    }
}

private fun previewState() = AccountTabState(
    totalBalance = dummyValueUi("203k"),
    excludedBalance = dummyValueUi("64,3k"),
    noAccounts = false,
    items = listOf(
        AccountListItemUi.AccountWithBalance(
            account = dummyAccountUi("Cash"),
            balance = dummyValueUi("240.75"),
            balanceBaseCurrency = null,
        ),
        AccountListItemUi.FolderWithAccounts(
            folder = dummyFolderUi("Business"),
            balance = dummyValueUi("5,320.50"),
            accItems = listOf(
                AccountListItemUi.AccountWithBalance(
                    account = dummyAccountUi("Account 1"),
                    balance = dummyValueUi("1,000.00", "BGN"),
                    balanceBaseCurrency = dummyValueUi("500")
                ),
                AccountListItemUi.AccountWithBalance(
                    account = dummyAccountUi("Account 2", color = Blue, excluded = true),
                    balance = dummyValueUi("0.00"),
                    balanceBaseCurrency = null
                ),
                AccountListItemUi.AccountWithBalance(
                    account = dummyAccountUi("Account 3", color = Red),
                    balance = dummyValueUi("4,320.50"),
                    balanceBaseCurrency = null
                ),
            ),
            accountsCount = 3,
        ),
        AccountListItemUi.AccountWithBalance(
            account = dummyAccountUi("Revolut", color = Blue),
            balance = dummyValueUi("1,032.54"),
            balanceBaseCurrency = null
        ),
        AccountListItemUi.Archived(
            accHolders = listOf(),
            accountsCount = 0,
        )
    ),
    createModal = IvyModal()
)
// endregion
