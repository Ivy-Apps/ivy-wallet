package com.ivy.core.ui.account.pick

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivy.core.ui.component.SelectableItem
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun ChooseAccountLazyColumn(
    modifier: Modifier = Modifier,
    selected: AccountUi?,
    onPickAccount: (AccountUi) -> Unit,
) {
    val viewModel: AccountPickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LazyColumn(
        modifier = modifier,
    ) {

    }
}

private fun LazyListScope.accounts(
    selected: AccountUi?,
    accounts: List<AccountUi>,
    onSelect: (AccountUi) -> Unit,
    onDeselect: (AccountUi) -> Unit
) {
    items(
        items = accounts,
        key = { it.id }
    ) { item ->

    }
}

@Composable
private fun AccountItem(
    selected: Boolean,
    item: AccountUi,
    onSelect: () -> Unit,
    onDeselect: () -> Unit,
) {
    SelectableItem(
        name = item.name,
        icon = item.icon,
        color = item.color,
        selected = selected,
        onSelect = onSelect,
        onDeselect = onDeselect,
    )
}


// region Preview
private fun previewState() = AccountPickerState(
    accounts = emptyList()
)
// endregion