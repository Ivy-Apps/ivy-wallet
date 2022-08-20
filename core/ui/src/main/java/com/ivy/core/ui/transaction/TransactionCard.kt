package com.ivy.core.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.core.ui.account.Badge
import com.ivy.core.ui.category.Badge
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.icon.IconSize
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.White
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor

@Composable
fun Transaction.Card(
    modifier: Modifier = Modifier,

    onClick: () -> Unit = {},
    onAccountClick: (Account) -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = onClick)
            .padding(all = 20.dp)
            .testTag("transaction_card")
    ) {
        TransactionHeader(
            type = type,
            account = account,
            category = category,
            onAccountClick = onAccountClick,
            onCategoryClick = onCategoryClick
        )
    }
}

@Composable
private fun TransactionHeader(
    type: TransactionType,
    account: Account,
    category: Category?,

    onAccountClick: (Account) -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    when (type) {
        is TransactionType.Transfer -> TransferHeader(
            account = account,
            toAccount = type.toAccount
        )
        else -> IncomeExpenseHeader(
            account = account,
            category = category,
            onAccountClick = onAccountClick,
            onCategoryClick = onCategoryClick,
        )
    }
}

@Composable
private fun IncomeExpenseHeader(
    account: Account,
    category: Category?,

    onCategoryClick: (Category) -> Unit,
    onAccountClick: (Account) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        category?.let {
            category.Badge(
                onClick = { onCategoryClick(category) }
            )
            SpacerHor(width = 12.dp)
        }

        account.Badge(
            background = UI.colors.pure,
            onClick = { onAccountClick(account) }
        )
    }
}

@Composable
private fun TransferHeader(
    account: Account,
    toAccount: Account
) {
    @Composable
    fun Account.IconName() {
        icon.ItemIcon(
            size = IconSize.S,
            tint = White,
        )
        SpacerHor(width = 4.dp)
        IvyText(
            text = name,
            typo = UI.typo.c.style(
                color = White,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 20.dp)
            .background(UI.colors.pure, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        account.IconName()

        Spacer(Modifier.width(12.dp))
        IvyIcon(icon = R.drawable.ic_arrow_right)
        Spacer(Modifier.width(8.dp))

        toAccount.IconName()
    }
}