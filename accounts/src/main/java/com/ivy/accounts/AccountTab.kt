package com.ivy.accounts

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.account.AccountBadge
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.data.dummyAccountUi
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.H2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@Composable
fun BoxScope.AccountTab() {
    val viewModel: AccountViewModel? = hiltViewmodelPreviewSafe()
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
        LazyColumn {
            items(items = state.accounts) { acc ->
                SpacerVer(height = 8.dp)
                AccountBadge(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    account = acc
                )
            }
            item {
                SpacerVer(height = 320.dp) // last item spacer
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
        UI(
            state = AccountTabState(
                accounts = listOf(
                    dummyAccountUi(name = "Account 1"),
                    dummyAccountUi(name = "Account 2"),
                    dummyAccountUi(name = "Account 3"),
                ),
                createAccountModal = rememberIvyModal()
            ),
            onEvent = {}
        )
    }
}

private fun previewState() = AccountTabState(
    accounts = listOf(
        dummyAccountUi(name = "Account 1"),
        dummyAccountUi(name = "Account 2"),
        dummyAccountUi(name = "Account 3"),
    ),
    createAccountModal = IvyModal()
)
// endregion
