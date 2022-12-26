package com.ivy.core.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.util.ComponentPreview

@Composable
fun AccountButton(
    account: AccountUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.fullyRounded)
            .background(account.color, UI.shapes.fullyRounded)
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 16.dp)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrast = rememberContrast(color = account.color)
        ItemIcon(
            itemIcon = account.icon,
            size = IconSize.S,
            tint = contrast,
        )
        B2(
            modifier = Modifier
                .padding(start = 4.dp)
                .widthIn(min = 0.dp, max = 120.dp),
            text = account.name,
            color = contrast,
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        AccountButton(
            account = dummyAccountUi(),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Long() {
    ComponentPreview {
        AccountButton(
            account = dummyAccountUi(
                name = "This is a very long account name, which should be on multiple lines",
            ),
            onClick = {},
        )
    }
}
// endregion