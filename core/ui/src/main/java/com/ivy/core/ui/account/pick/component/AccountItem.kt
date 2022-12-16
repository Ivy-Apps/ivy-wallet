package com.ivy.core.ui.account.pick.component

import androidx.compose.runtime.Composable
import com.ivy.core.ui.account.pick.data.SelectableAccountUi
import com.ivy.core.ui.component.SelectableItem

@Composable
fun AccountItem(
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


// TODO: Add previews
