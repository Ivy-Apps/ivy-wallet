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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.R
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.core.ui.value.AmountCurrencySmall
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview

@Composable
fun AccountCard(
    account: AccountUi,
    balance: ValueUi,
    balanceBaseCurrency: ValueUi?,
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
            .padding(top = 4.dp, bottom = 12.dp),
    ) {
        val contrastColor = rememberContrast(account.color)
        Header(account = account, color = contrastColor, dynamicContrast = dynamicContrast)
        SpacerVer(height = 4.dp)
        Balance(balance = balance, color = contrastColor)
        BalanceBaseCurrency(balanceBaseCurrency = balanceBaseCurrency, color = dynamicContrast)
    }
}

@Composable
private fun Header(
    account: AccountUi,
    color: Color,
    dynamicContrast: Color,
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
        if (account.excluded) {
            SpacerHor(width = 4.dp)
            Caption(text = stringResource(R.string.excluded), color = dynamicContrast)
        }
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

@Composable
private fun BalanceBaseCurrency(
    balanceBaseCurrency: ValueUi?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (balanceBaseCurrency != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .padding(start = 14.dp), // so it looks aligned with the balance
            verticalAlignment = Alignment.CenterVertically
        ) {
            AmountCurrencySmall(value = balanceBaseCurrency, color = color)
        }
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        AccountCard(
            account = dummyAccountUi(excluded = true),
            balance = dummyValueUi("1,324.50"),
            balanceBaseCurrency = dummyValueUi("2,972.95", "BGN")
        ) {

        }
    }
}
// endregion