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
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.core.domain.pure.format.dummyFormattedValue
import com.ivy.core.ui.R
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.transaction.TrnListItemUi.Transfer
import com.ivy.core.ui.data.transaction.dummyTransactionUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l1_buildingBlocks.Icon
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.B2Second
import com.ivy.design.l2_components.C
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
    fun AccountUi.IconName() {
        icon.ItemIcon(
            size = IconSize.S,
            tint = UI.colorsInverted.pure,
        )
        SpacerHor(width = 4.dp)
        name.C(
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
        account.IconName()

        SpacerHor(width = 12.dp)
        R.drawable.ic_arrow_right.Icon()
        SpacerHor(width = 8.dp)

        toAccount.IconName()
    }
}

@Composable
private fun TransferAmount(
    fromValue: FormattedValue,
) {
    SpacerVer(height = 12.dp)
    TransactionCardAmountRow {
        R.drawable.ic_transfer.Icon(
            modifier = Modifier.background(UI.colors.primary, UI.shapes.circle),
            tint = rememberContrastColor(UI.colors.primary),
        )
        SpacerHor(width = 12.dp)
        fromValue.AmountCurrency(color = UI.colors.primary)
    }
}

@Composable
private fun ToAmountDifferentCurrency(
    fromCurrency: CurrencyCode,
    toValue: FormattedValue,
) {
    if (fromCurrency != toValue.currency) {
        "${toValue.amount} ${toValue.currency}".B2Second(
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
                type = TrnType.Expense,
                value = dummyFormattedValue(amount = "400")
            ),
            to = dummyTransactionUi(
                type = TrnType.Expense,
                value = dummyFormattedValue(amount = "400")
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
                type = TrnType.Expense,
                value = dummyFormattedValue(amount = "400", currency = "EUR")
            ),
            to = dummyTransactionUi(
                type = TrnType.Expense,
                value = dummyFormattedValue(amount = "800", currency = "BGN")
            ),
            fee = null
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {}
        )
    }
}
// endregion