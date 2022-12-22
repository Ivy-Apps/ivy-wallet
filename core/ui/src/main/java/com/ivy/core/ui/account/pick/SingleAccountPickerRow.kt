package com.ivy.core.ui.account.pick

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.account.pick.component.SelectableAccountItem
import com.ivy.core.ui.account.pick.data.SelectableAccountUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun SingleAccountPickerRow(
    modifier: Modifier = Modifier,
    selected: AccountUi,
    onSelectedChange: (AccountUi) -> Unit,
) {
    val viewModel: AccountPickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    val listState = rememberLazyListState()

    LaunchedEffect(selected) {
        viewModel?.onEvent(AccountPickerEvent.SelectedChange(listOf(selected)))
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = listState,
    ) {

        accountItems(
            items = state.accounts,
            onSelectedChange = { acc ->
                viewModel?.onEvent(AccountPickerEvent.SelectedChange(listOf(acc)))
                onSelectedChange(acc)
            }
        )
    }
}

private fun LazyListScope.accountItems(
    items: List<SelectableAccountUi>,
    onSelectedChange: (AccountUi) -> Unit,
) {
    items(
        items = items,
        key = { it.account.id }
    ) { item ->
        SpacerHor(width = 8.dp)
        SelectableAccountItem(
            item = item,
            deselectButton = false,
            onSelect = { onSelectedChange(item.account) },
            onDeselect = {
                // do nothing because we always want to have a selected account
            }
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
            onSelectedChange = {}
        )
    }
}

private fun previewState() = AccountPickerState(
    accounts = listOf(
        dummyAccountUi("Account 1"),
        dummyAccountUi("Account 2", color = Blue),
        dummyAccountUi("DSK", color = Green),
        dummyAccountUi("Unicredit Bulbank", color = Red),
        dummyAccountUi("Revolut", color = Purple2Dark),
        dummyAccountUi("Investments", color = Green2Dark),
        dummyAccountUi("Cash", color = Green2Dark),
    ).mapIndexed { index, acc ->
        SelectableAccountUi(acc, selected = index == 0)
    }
)
// endregion
