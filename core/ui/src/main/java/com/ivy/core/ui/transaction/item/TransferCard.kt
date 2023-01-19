package com.ivy.core.ui.transaction.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.R
import com.ivy.core.ui.algorithm.trnhistory.data.TransferUi
import com.ivy.core.ui.category.CategoryBadge
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.util.ComponentPreview

@Composable
fun TransferCard(
    transfer: TransferUi,
    modifier: Modifier = Modifier,
    onAccountClick: (AccountUi) -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onClick: (TransferUi) -> Unit,
    onExecuteTransfer: (TransferUi) -> Unit,
    onSkipTransfer: (TransferUi) -> Unit,
) {
    BaseTrnCard(
        modifier = modifier,
        onClick = { onClick(transfer) }
    ) {
        TransferHeader(
            fromAccount = transfer.fromAccount,
            toAccount = transfer.toAccount,
            onAccountClick = onAccountClick
        )
        Category(category = transfer.category, onCategoryClick = onCategoryClick)
        DueDate(time = transfer.time)
        Title(title = transfer.title, time = transfer.time)
        Description(description = transfer.description, title = transfer.title)
        TransferAmount(fromValue = transfer.fromAmount)
        ToAmountReceived(
            fromValue = transfer.fromAmount,
            toValue = transfer.toAmount
        )
        Fee(fee = transfer.fee)
        DuePaymentCTAs(
            time = transfer.time,
            cta = "Execute",
            onSkip = {
                onSkipTransfer(transfer)
            },
            onExecute = {
                onExecuteTransfer(transfer)
            },
        )
    }
}

@Composable
private fun TransferHeader(
    fromAccount: AccountUi,
    toAccount: AccountUi,
    onAccountClick: (AccountUi) -> Unit,
) {
    @Composable
    fun IconAndName(
        account: AccountUi,
        horizontalArrangement: Arrangement.Horizontal,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Row(
            modifier = modifier
                .clip(UI.shapes.fullyRounded)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
        ) {
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
    }

    Row(
        modifier = Modifier
            .background(UI.colors.pure, UI.shapes.fullyRounded)
            .padding(start = 8.dp, end = 20.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconAndName(
            modifier = Modifier.weight(1f),
            account = fromAccount,
            horizontalArrangement = Arrangement.Start,
            onClick = { onAccountClick(fromAccount) }
        )

        SpacerHor(width = 12.dp)
        IconRes(R.drawable.ic_arrow_right)
        SpacerHor(width = 8.dp)

        IconAndName(
            modifier = Modifier.weight(1f),
            account = toAccount,
            horizontalArrangement = Arrangement.End,
            onClick = { onAccountClick(toAccount) }
        )
    }
}

@Composable
private fun Category(
    category: CategoryUi?,
    onCategoryClick: (CategoryUi) -> Unit,
) {
    if (category != null) {
        SpacerVer(height = 8.dp)
        CategoryBadge(category = category, onClick = { onCategoryClick(category) })
    }
}

@Composable
private fun TransferAmount(
    fromValue: ValueUi,
) {
    SpacerVer(height = 8.dp)
    TransactionCardAmountRow {
        IconRes(
            modifier = Modifier.background(UI.colors.primary, UI.shapes.circle),
            icon = R.drawable.ic_transfer,
            tint = rememberContrast(UI.colors.primary),
        )
        SpacerHor(width = 12.dp)
        AmountCurrency(value = fromValue, color = UI.colors.primary)
    }
}

@Composable
private fun ToAmountReceived(
    fromValue: ValueUi,
    toValue: ValueUi?,
) {
    if (toValue != null && fromValue != toValue) {
        B2Second(
            text = "${toValue.amount} ${toValue.currency}",
            modifier = Modifier.padding(start = 44.dp),
            color = UI.colors.neutral,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun Fee(
    fee: ValueUi?,
) {
    if (fee != null) {
        SpacerVer(height = 8.dp)
        TransactionCardAmountRow {
            IconRes(
                modifier = Modifier.background(UI.colors.red, UI.shapes.circle),
                icon = R.drawable.ic_expense,
                tint = White
            )
            SpacerHor(width = 12.dp)
            AmountCurrency(value = fee, color = UI.colors.red)
            SpacerHor(width = 4.dp)
            B2Second(
                text = "(FEE)",
                color = UI.colors.red,
                fontWeight = FontWeight.Normal
            )
        }
    }
}


// region Previews
@Preview
@Composable
private fun Preview_SameCurrency() {
    ComponentPreview {
        TransferCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            transfer = TransferUi(
                batchId = "",
                time = dummyTrnTimeActualUi(),
                fromAmount = dummyValueUi("400"),
                toAmount = dummyValueUi("400"),
                fee = null,
                category = null,
                description = null,
                title = null,
                fromAccount = dummyAccountUi(),
                toAccount = dummyAccountUi(),
            ),
            onAccountClick = {},
            onCategoryClick = {},
            onClick = {},
            onSkipTransfer = {},
            onExecuteTransfer = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Detailed() {
    ComponentPreview {
        TransferCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            transfer = TransferUi(
                batchId = "",
                time = dummyTrnTimeActualUi(),
                title = "Withdrawing cash",
                description = "So I can pay rent",
                category = dummyCategoryUi(),
                fromAmount = dummyValueUi(amount = "400", currency = "EUR"),
                fromAccount = dummyAccountUi(),
                toAmount = dummyValueUi(amount = "800", currency = "BGN"),
                toAccount = dummyAccountUi(),
                fee = dummyValueUi("2")
            ),
            onAccountClick = {},
            onCategoryClick = {},
            onClick = {},
            onExecuteTransfer = {},
            onSkipTransfer = {}
        )
    }
}

@Preview
@Composable
private fun Preview_LongAccount_names() {
    ComponentPreview {
        TransferCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            transfer = TransferUi(
                batchId = "",
                time = dummyTrnTimeActualUi(),
                fromAccount = dummyAccountUi(name = "My very long account name"),
                fromAmount = dummyValueUi(amount = "400", currency = "EUR"),
                toAccount = dummyAccountUi(name = "Revolut Business Company Account"),
                toAmount = dummyValueUi(amount = "800", currency = "BGN"),
                fee = null,
                title = null,
                description = null,
                category = null,
            ),
            onAccountClick = {},
            onCategoryClick = {},
            onClick = {},
            onSkipTransfer = {},
            onExecuteTransfer = {}
        )
    }
}
// endregion