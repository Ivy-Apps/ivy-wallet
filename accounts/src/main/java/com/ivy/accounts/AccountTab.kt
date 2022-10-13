package com.ivy.accounts

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.components.accountItemsList
import com.ivy.accounts.data.AccListItemUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.data.account.dummyAccountFolderUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.H2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@Composable
fun BoxScope.AccountTab() {
    val viewModel: AccountTabViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value
        ?: previewState()

    UI(state = state, onEvent = { viewModel?.onEvent(it) })
}

@Composable
private fun BoxScope.UI(
    state: AccountTabState,
    onEvent: (AccountTabEvent) -> Unit,
) {
    ColumnRoot {
        H2(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(start = 24.dp),
            text = "Accounts"
        )
        SpacerVer(height = 8.dp)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            accountItemsList(
                items = state.items,
                onAccountClick = {
                    // TODO: Implement
                },
                onFolderClick = {
                    // TODO: Implement
                }
            )
            item {
                SpacerVer(height = 300.dp) // last item spacer
            }
        }
    }

    CreateAccountModal(modal = state.createAccountModal)
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
    items = listOf(
        AccListItemUi.AccountHolder(
            account = dummyAccountUi("Cash"),
            balance = dummyValueUi("240.75"),
            balanceBaseCurrency = null,
        ),
        AccListItemUi.FolderHolder(
            folder = dummyAccountFolderUi("Business"),
            balance = dummyValueUi("5,320.50"),
            accItems = listOf(
                AccListItemUi.AccountHolder(
                    account = dummyAccountUi("Account 1"),
                    balance = dummyValueUi("1,000.00", "BGN"),
                    balanceBaseCurrency = dummyValueUi("500")
                ),
                AccListItemUi.AccountHolder(
                    account = dummyAccountUi("Account 2", color = Blue, excluded = true),
                    balance = dummyValueUi("0.00"),
                    balanceBaseCurrency = null
                ),
                AccListItemUi.AccountHolder(
                    account = dummyAccountUi("Account 3", color = Red),
                    balance = dummyValueUi("4,320.50"),
                    balanceBaseCurrency = null
                ),
            )
        ),
        AccListItemUi.AccountHolder(
            account = dummyAccountUi("Revolut", color = Blue),
            balance = dummyValueUi("1,032.54"),
            balanceBaseCurrency = null
        ),
    ),
    createAccountModal = IvyModal()
)
// endregion
