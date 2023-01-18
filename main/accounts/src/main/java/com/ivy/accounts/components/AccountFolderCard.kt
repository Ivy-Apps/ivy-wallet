package com.ivy.accounts.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.R
import com.ivy.accounts.data.AccountListItemUi.AccountWithBalance
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.account.dummyFolderUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.isInPreview


@Composable
fun AccountFolderCard(
    folder: FolderUi,
    balance: ValueUi,
    accounts: List<AccountWithBalance>,
    accountsCount: Int,
    modifier: Modifier = Modifier,
    onAccountClick: (AccountUi) -> Unit,
    onFolderClick: () -> Unit,
) {
    val dynamicContrast = rememberDynamicContrast(folder.color)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(UI.shapes.rounded)
            .border(1.dp, dynamicContrast, UI.shapes.rounded)
            .clickable(onClick = onFolderClick),
    ) {
        val contrastColor = rememberContrast(folder.color)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(folder.color, UI.shapes.roundedTop)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 12.dp)
        ) {
            IconNameRow(folderName = folder.name, folderIcon = folder.icon, color = contrastColor)
            SpacerVer(height = 2.dp)
            Balance(balance = balance, color = contrastColor)
        }
        var expanded by if (isInPreview()) remember {
            mutableStateOf(previewExpanded)
        } else remember { mutableStateOf(false) }
        ExpandCollapse(
            expanded = expanded,
            color = UI.colorsInverted.pure,
            accountsCount = accountsCount,
            onSetExpanded = { expanded = it }
        )
        Accounts(expanded = expanded, items = accounts, onClick = onAccountClick)
    }
}

@Composable
private fun IconNameRow(
    folderName: String,
    folderIcon: ItemIcon,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIcon(
            itemIcon = folderIcon,
            size = IconSize.M,
            tint = color,
        )
        SpacerHor(width = 8.dp)
        B2(
            modifier = Modifier.weight(1f),
            text = folderName,
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

@Composable
private fun ExpandCollapse(
    expanded: Boolean,
    color: Color,
    accountsCount: Int,
    onSetExpanded: (Boolean) -> Unit
) {
    if (accountsCount > 0) {
        IvyButton(
            size = ButtonSize.Big,
            shape = UI.shapes.roundedBottom,
            visibility = Visibility.Low,
            feeling = Feeling.Custom(color),
            text = if (expanded)
                "Tap to collapse ($accountsCount)" else "Tap to expand ($accountsCount)",
            icon = if (expanded)
                R.drawable.ic_round_expand_less_24 else R.drawable.round_expand_more_24
        ) {
            onSetExpanded(!expanded)
        }
    } else {
        B2(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            text = "Empty folder",
            fontWeight = FontWeight.ExtraBold,
            color = UI.colors.neutral,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
private fun Accounts(
    expanded: Boolean,
    items: List<AccountWithBalance>,
    onClick: (AccountUi) -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column(Modifier.fillMaxWidth()) {
            items.forEach {
                key("${it.account.id}${it.balance.amount}") {
                    AccountCard(
                        account = it.account,
                        balance = it.balance,
                        balanceBaseCurrency = it.balanceBaseCurrency,
                        onClick = { onClick(it.account) }
                    )
                    SpacerVer(height = 8.dp)
                }
            }
            SpacerVer(height = 4.dp)
        }
    }
}


// region Preview
private var previewExpanded = false

@Preview
@Composable
private fun Preview_Collapsed() {
    ComponentPreview {
        AccountFolderCard(
            folder = dummyFolderUi("Business"),
            balance = dummyValueUi("5,320.50"),
            accounts = emptyList(),
            accountsCount = 0,
            onAccountClick = {},
            onFolderClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Expanded() {
    ComponentPreview {
        previewExpanded = true
        AccountFolderCard(
            folder = dummyFolderUi("Business"),
            balance = dummyValueUi("5,320.50"),
            accounts = listOf(
                AccountWithBalance(
                    account = dummyAccountUi("Account 1"),
                    balance = dummyValueUi("1,000.00", "ADA"),
                    balanceBaseCurrency = dummyValueUi("358.76")
                ),
                AccountWithBalance(
                    account = dummyAccountUi("Account 2", color = Blue, excluded = true),
                    balance = dummyValueUi("0.00"),
                    balanceBaseCurrency = null
                ),
                AccountWithBalance(
                    account = dummyAccountUi("Account 3", color = Red),
                    balance = dummyValueUi("4,320.50"),
                    balanceBaseCurrency = null
                ),
            ),
            accountsCount = 3,
            onAccountClick = {},
            onFolderClick = {},
        )
    }
}
// endregion