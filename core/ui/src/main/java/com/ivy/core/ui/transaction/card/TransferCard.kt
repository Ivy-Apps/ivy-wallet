package com.ivy.core.ui.transaction.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.R
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.transaction.TrnListItemUi.Transfer
import com.ivy.core.ui.data.transaction.dummyTransactionUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.util.ComponentPreview

@Composable
fun Transfer.Card(
    onClick: (Transfer) -> Unit,
    modifier: Modifier = Modifier,
) {
    TransactionCard(
        modifier = modifier,
        onClick = { onClick(this@Card) }
    ) {
        TransferHeader(account = from.account, toAccount = to.account)
        DueDate(time = time)
        Title(title = from.title, time = time)
        Description(description = from.description, title = from.title)
        TransferAmount(fromValue = from.value)
        ToAmountDifferentCurrency(fromCurrency = from.value.currency, toValue = to.value)
    }
}

@Composable
private fun TransferHeader(
    account: AccountUi,
    toAccount: AccountUi
) {
    @Composable
    fun IconAndName(account: AccountUi) {
        ItemIcon(
            itemIcon = account.icon,
            size = IconSize.S,
            tint = UI.colorsInverted.pure,
        )
        SpacerHor(width = 4.dp)
        Caption(
            text = account.name,
            color = UI.colorsInverted.pure,
            fontWeight = FontWeight.ExtraBold
        )
    }

    Row(
        modifier = Modifier
            .background(UI.colors.pure, UI.shapes.fullyRounded)
            .padding(start = 8.dp, end = 20.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconAndName(account)

        SpacerHor(width = 12.dp)
        IconRes(R.drawable.ic_arrow_right)
        SpacerHor(width = 8.dp)

        IconAndName(account)
    }
}

@Composable
private fun TransferAmount(
    fromValue: ValueUi,
) {
    SpacerVer(height = 12.dp)
    TransactionCardAmountRow {
        IconRes(
            modifier = Modifier.background(UI.colors.primary, UI.shapes.circle),
            icon = R.drawable.ic_transfer,
            tint = rememberContrastColor(UI.colors.primary),
        )
        SpacerHor(width = 12.dp)
        AmountCurrency(value = fromValue, color = UI.colors.primary)
    }
}

@Composable
private fun ToAmountDifferentCurrency(
    fromCurrency: CurrencyCode,
    toValue: ValueUi,
) {
    if (fromCurrency != toValue.currency) {
        B2Second(
            text = "${toValue.amount} ${toValue.currency}",
            modifier = Modifier.padding(start = 48.dp),
            color = UI.colors.neutral,
            fontWeight = FontWeight.Normal,
        )
    }
}


// region Previews
@Preview
@Composable
private fun Preview_SameCurrency() {
    ComponentPreview {
        Transfer(
            batchId = "",
            time = dummyTrnTimeActualUi(),
            from = dummyTransactionUi(
                type = TransactionType.Expense,
                value = dummyValueUi(amount = "400")
            ),
            to = dummyTransactionUi(
                type = TransactionType.Expense,
                value = dummyValueUi(amount = "400")
            ),
            fee = null
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Detailed() {
    ComponentPreview {
        Transfer(
            batchId = "",
            time = dummyTrnTimeActualUi(),
            from = dummyTransactionUi(
                title = "Withdrawing cash",
                description = "So I can pay rent",
                type = TransactionType.Expense,
                value = dummyValueUi(amount = "400", currency = "EUR")
            ),
            to = dummyTransactionUi(
                type = TransactionType.Expense,
                value = dummyValueUi(amount = "800", currency = "BGN")
            ),
            fee = null
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {}
        )
    }
}
// endregion