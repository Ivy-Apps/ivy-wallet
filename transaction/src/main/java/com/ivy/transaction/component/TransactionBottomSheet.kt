package com.ivy.transaction.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.dummy.dummyValue
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.account.AccountButton
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.account.pick.SingleAccountPickerModal
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.value.AmountCurrencyBig
import com.ivy.core.ui.value.AmountCurrencySmall
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R
import com.ivy.transaction.modal.AmountModalWithAccounts

@Composable
internal fun BoxScope.AmountAccountSheet(
    amountUi: ValueUi,
    amount: Value,
    amountBaseCurrency: ValueUi?,
    account: AccountUi,
    ctaText: String,
    @DrawableRes
    ctaIcon: Int,
    accountPickerModal: IvyModal,
    amountModal: IvyModal,
    modifier: Modifier = Modifier,
    secondaryActions: (@Composable RowScope.() -> Unit)? = null,
    onAccountChange: (AccountUi) -> Unit,
    onAmountEnter: (Value) -> Unit,
    onCtaClick: () -> Unit,
) {
    BaseBottomSheet(
        modifier = modifier,
        ctaText = ctaText,
        ctaIcon = ctaIcon,
        onCtaClick = onCtaClick,
        secondaryActions = secondaryActions,
    ) {
        AmountAccountRow(
            amount = amountUi,
            amountBaseCurrency = amountBaseCurrency,
            account = account,
            onAmountClick = {
                amountModal.show()
            },
            onAccountClick = {
                accountPickerModal.show()
            }
        )
        SpacerVer(height = 16.dp)
    }

    Modals(
        account = account,
        accountPickerModal = accountPickerModal,
        amount = amount,
        amountModal = amountModal,
        onAccountChange = onAccountChange,
        onAmountEnter = onAmountEnter,
    )
}

@Composable
private fun AmountAccountRow(
    amount: ValueUi,
    amountBaseCurrency: ValueUi?,
    account: AccountUi,
    modifier: Modifier = Modifier,
    onAmountClick: () -> Unit,
    onAccountClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .clickable(onClick = onAmountClick)
                .padding(start = 8.dp),
        ) {
            AmountCurrencyBig(value = amount)
            if (amountBaseCurrency != null) {
                SpacerVer(height = 4.dp)
                Row {
                    AmountCurrencySmall(
                        value = amountBaseCurrency,
                        color = UI.colors.primary,
                    )
                }
            }
        }
        AccountButton(
            account = account,
            onClick = onAccountClick
        )
    }
}

@Composable
private fun BoxScope.Modals(
    account: AccountUi,
    accountPickerModal: IvyModal,
    amount: Value,
    amountModal: IvyModal,
    onAccountChange: (AccountUi) -> Unit,
    onAmountEnter: (Value) -> Unit,
) {
    SingleAccountPickerModal(
        modal = accountPickerModal,
        selected = account,
        onSelectAccount = onAccountChange
    )

    val createAccountModal = rememberIvyModal()
    AmountModalWithAccounts(
        modal = amountModal,
        amount = amount,
        account = account,
        onAddAccount = {
            createAccountModal.show()
        },
        onAmountEnter = onAmountEnter,
        onAccountChange = onAccountChange
    )

    CreateAccountModal(
        modal = createAccountModal,
        level = 2,
    )
}

// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        AmountAccountSheet(
            amountUi = dummyValueUi(),
            amount = dummyValue(),
            amountBaseCurrency = null,
            account = dummyAccountUi(),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            accountPickerModal = rememberIvyModal(),
            amountModal = rememberIvyModal(),
            onAccountChange = {},
            onAmountEnter = {},
            onCtaClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_LongAmount() {
    IvyPreview {
        AmountAccountSheet(
            amountUi = dummyValueUi(amount = "12345678901234567890.33"),
            amount = dummyValue(),
            amountBaseCurrency = dummyValueUi(
                amount = "12345678901234567890.33",
                currency = "BGN",
            ),
            account = dummyAccountUi(),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            accountPickerModal = rememberIvyModal(),
            amountModal = rememberIvyModal(),
            onAccountChange = {},
            onAmountEnter = {},
            onCtaClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_LongAmount_LongAccount() {
    IvyPreview {
        AmountAccountSheet(
            amountUi = dummyValueUi(amount = "12345678901234567890.33"),
            amount = dummyValue(),
            amountBaseCurrency = dummyValueUi(),
            account = dummyAccountUi(
                name = "Revolut Business Company 2 Account"
            ),
            ctaText = "Add",
            ctaIcon = R.drawable.ic_round_add_24,
            accountPickerModal = rememberIvyModal(),
            amountModal = rememberIvyModal(),
            onAccountChange = {},
            onAmountEnter = {},
            onCtaClick = {},
        )
    }
}

// endregion