package com.ivy.core.ui.account.pick

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.ivy.design.l3_ivyComponents.WrapContentRow
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun AccountPickerColumn(
    modifier: Modifier = Modifier,
    selected: List<AccountUi>,
    deselectButton: Boolean,
    onSelectAccount: (AccountUi) -> Unit,
    onDeselectAccount: (AccountUi) -> Unit,
) {
    val viewModel: AccountPickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LaunchedEffect(selected) {
        viewModel?.onEvent(AccountPickerEvent.SelectedChange(selected))
    }

    WrapContentRow(
        modifier = modifier,
        items = state.accounts,
        itemKey = { it.account.id },
        horizontalMarginBetweenItems = 8.dp,
        verticalMarginBetweenRows = 12.dp
    ) { item ->
        SelectableAccountItem(
            item = item,
            deselectButton = deselectButton,
            onSelect = { onSelectAccount(item.account) },
            onDeselect = { onDeselectAccount(item.account) }
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        AccountPickerColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            selected = listOf(),
            deselectButton = true,
            onSelectAccount = {},
            onDeselectAccount = {},
        )
    }
}

@Preview
@Composable
private fun Preview_noDeselect() {
    ComponentPreview {
        AccountPickerColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            selected = listOf(),
            deselectButton = false,
            onSelectAccount = {},
            onDeselectAccount = {},
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
        SelectableAccountUi(acc, selected = index % 3 == 0)
    }
)
// endregion