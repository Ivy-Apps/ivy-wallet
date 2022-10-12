package com.ivy.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview

@Composable
fun AccountCard(
    account: AccountUi,
    balance: ValueUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val dynamicContrast = rememberDynamicContrast(account.color)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(UI.shapes.rounded)
            .background(account.color, UI.shapes.rounded)
            .border(1.dp, dynamicContrast, UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        val contrastColor = rememberContrastColor(account.color)
        IconNameRow(account = account, color = contrastColor)
        SpacerVer(height = 4.dp)
        Balance(balance = balance, color = contrastColor)
    }
}

@Composable
private fun IconNameRow(
    account: AccountUi,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIcon(
            itemIcon = account.icon,
            size = IconSize.M,
            tint = color,
        )
        SpacerHor(width = 8.dp)
        B2(
            modifier = Modifier.weight(1f),
            text = account.name,
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun Balance(
    balance: ValueUi,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmountCurrency(value = balance, color = color)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        AccountCard(
            account = dummyAccountUi(),
            balance = dummyValueUi("1,324.50")
        ) {

        }
    }
}
// endregion