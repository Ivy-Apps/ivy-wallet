package com.ivy.transaction.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.account.AccountButton
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.value.AmountCurrencyBig
import com.ivy.design.l0_system.color.Blue2Dark
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview
import com.ivy.transaction.R

@Composable
fun BoxScope.TransferBottomSheet(
    accountFrom: AccountUi,
    amountFrom: ValueUi,
    accountTo: AccountUi,
    amountTo: ValueUi,

    modifier: Modifier = Modifier,
    secondaryActions: (@Composable RowScope.() -> Unit)? = null,
    ctaText: String,
    @DrawableRes
    ctaIcon: Int,
    onCtaClick: () -> Unit,
) {
    BaseBottomSheet(
        modifier = modifier,
        ctaText = ctaText,
        ctaIcon = ctaIcon,
        secondaryActions = secondaryActions,
        onCtaClick = onCtaClick
    ) {
        TransferContent(
            accountFrom = accountFrom,
            amountFrom = amountFrom,
            accountTo = accountTo,
            amountTo = amountTo,

            onFromAccountClick = {},
            onFromAmountClick = {},
            onToAccountClick = {},
            onToAmountClick = {},
        )
        SpacerVer(height = 16.dp)
    }
}

@Composable
private fun TransferContent(
    accountFrom: AccountUi,
    amountFrom: ValueUi,
    accountTo: AccountUi,
    amountTo: ValueUi,

    onFromAccountClick: () -> Unit,
    onFromAmountClick: () -> Unit,
    onToAccountClick: () -> Unit,
    onToAmountClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountAmount(
            modifier = Modifier.weight(1f),
            label = "From",
            alignment = Alignment.Start,
            account = accountFrom,
            amount = amountFrom,
            onAccountClick = onFromAccountClick,
            onAmountClick = onFromAmountClick,
        )
        IconRes(icon = R.drawable.ic_arrow_right)
        AccountAmount(
            modifier = Modifier.weight(1f),
            label = "To",
            alignment = Alignment.End,
            account = accountTo,
            amount = amountTo,
            onAccountClick = onToAccountClick,
            onAmountClick = onToAmountClick,
        )
    }
}

@Composable
private fun AccountAmount(
    account: AccountUi,
    amount: ValueUi,
    label: String,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit,
    onAmountClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalAlignment = alignment
    ) {
        B1(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = label,
            fontWeight = FontWeight.SemiBold,
        )
        SpacerVer(height = 8.dp)
        AccountButton(account = account, onClick = onAccountClick)
        Column(
            modifier = Modifier
                .clickable(onClick = onAmountClick)
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            horizontalAlignment = alignment,
        ) {
            AmountCurrencyBig(value = amount)
        }
    }
}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        TransferBottomSheet(
            accountFrom = dummyAccountUi(),
            amountFrom = dummyValueUi(),
            accountTo = dummyAccountUi(),
            amountTo = dummyValueUi(),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            onCtaClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Normal() {
    IvyPreview {
        TransferBottomSheet(
            accountFrom = dummyAccountUi(
                name = "DSK Bank",
                color = Blue2Dark,
            ),
            amountFrom = dummyValueUi(
                amount = "400.00"
            ),
            accountTo = dummyAccountUi(
                name = "Cash",
            ),
            amountTo = dummyValueUi(
                amount = "400.00"
            ),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            onCtaClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_LongMultiCurrency() {
    IvyPreview {
        TransferBottomSheet(
            accountFrom = dummyAccountUi(
                name = "Revolut Business EUR",
                color = Blue2Dark,
            ),
            amountFrom = dummyValueUi(
                amount = "160,235.30",
                currency = "EUR"
            ),
            accountTo = dummyAccountUi(
                name = "Bank Company Account BGN",
            ),
            amountTo = dummyValueUi(
                amount = "310,818.94",
                currency = "BGN",
            ),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            onCtaClick = {}
        )
    }
}
// endregion