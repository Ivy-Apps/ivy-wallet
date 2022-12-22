package com.ivy.core.ui.account.pick.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.account.pick.data.SelectableAccountUi
import com.ivy.core.ui.component.SelectableItem
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.design.util.ComponentPreview

@Composable
fun SelectableAccountItem(
    item: SelectableAccountUi,
    deselectButton: Boolean,
    onSelect: () -> Unit,
    onDeselect: () -> Unit,
) {
    SelectableItem(
        name = item.account.name,
        icon = item.account.icon,
        color = item.account.color,
        selected = item.selected,
        deselectButton = deselectButton,
        onSelect = onSelect,
        onDeselect = onDeselect,
    )
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        SelectableAccountItem(
            item = SelectableAccountUi(
                account = dummyAccountUi(),
                selected = true,
            ),
            deselectButton = true,
            onSelect = {},
            onDeselect = {},
        )
    }
}
