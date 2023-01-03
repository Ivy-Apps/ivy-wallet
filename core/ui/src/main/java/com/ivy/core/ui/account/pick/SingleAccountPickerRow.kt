package com.ivy.core.ui.account.pick

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.pick.component.SelectableAccountItem
import com.ivy.core.ui.account.pick.data.SelectableAccountUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.lastItemSpacerHorizontal
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun SingleAccountPickerRow(
    modifier: Modifier = Modifier,
    selected: AccountUi,
    onAddAccount: () -> Unit,
    onSelectedChange: (AccountUi) -> Unit,
) {
    val viewModel: AccountsViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    val listState = rememberLazyListState()

    LaunchedEffect(selected, state.accounts) {
        state.accounts.indexOfFirst { it.id == selected.id }
            .let { index ->
                if (index != -1) {
                    listState.animateScrollToItem(index)
                }
            }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        accountItems(
            items = state.accounts,
            selected = selected,
            onSelectedChange = onSelectedChange,
        )
        addAccount(onAddAccount)
        lastItemSpacerHorizontal(width = 12.dp)
    }
}

private fun LazyListScope.accountItems(
    items: List<AccountUi>,
    selected: AccountUi,
    onSelectedChange: (AccountUi) -> Unit,
) {
    items(
        items = items,
        key = { it.id }
    ) { item ->
        SpacerHor(width = 8.dp)
        SelectableAccountItem(
            item = SelectableAccountUi(
                account = item,
                selected = item.id == selected.id,
            ),
            deselectButton = false,
            onSelect = { onSelectedChange(item) },
            onDeselect = {
                // do nothing because we always want to have a selected account
            }
        )
    }
}

private fun LazyListScope.addAccount(
    onClick: () -> Unit
) {
    item(key = "add_account") {
        SpacerHor(width = 8.dp)
        IvyButton(
            size = ButtonSize.Small,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = stringResource(R.string.add_account),
            icon = R.drawable.ic_round_add_24,
            onClick = onClick
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        SingleAccountPickerRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            selected = dummyAccountUi(),
            onAddAccount = {},
            onSelectedChange = {},
        )
    }
}

private fun previewState() = AccountsState(
    accounts = listOf(
        dummyAccountUi("Account 1"),
        dummyAccountUi("Account 2", color = Blue),
        dummyAccountUi("DSK", color = Green),
        dummyAccountUi("Unicredit Bulbank", color = Red),
        dummyAccountUi("Revolut", color = Purple2Dark),
        dummyAccountUi("Investments", color = Green2Dark),
        dummyAccountUi("Cash", color = Green2Dark),
    )
)
// endregion
