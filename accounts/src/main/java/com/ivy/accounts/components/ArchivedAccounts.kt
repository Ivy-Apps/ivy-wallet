package com.ivy.accounts.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.R
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.isInPreview

@Composable
internal fun ArchivedAccounts(
    archived: AccountListItemUi.Archived,
    onAccountClick: (AccountUi) -> Unit,
) {
    var expanded by if (isInPreview()) remember {
        mutableStateOf(previewExpanded)
    } else remember { mutableStateOf(false) }
    ArchivedDivider(
        expanded = expanded,
        accountsCount = archived.accountsCount,
        onSetExpanded = { expanded = it }
    )
    AccountsList(
        accounts = archived.accHolders,
        expanded = expanded,
        onAccountClick = onAccountClick
    )
}

@Composable
private fun ArchivedDivider(
    expanded: Boolean,
    accountsCount: Int,
    onSetExpanded: (Boolean) -> Unit
) {
    IvyButton(
        size = ButtonSize.Big,
        visibility = Visibility.Low,
        feeling = Feeling.Neutral,
        text = "Archived ($accountsCount)",
        icon = if (expanded)
            R.drawable.round_expand_more_24 else R.drawable.ic_round_expand_less_24
    ) {
        onSetExpanded(!expanded)
    }
}

@Composable
private fun AccountsList(
    accounts: List<AccountListItemUi.AccountWithBalance>,
    expanded: Boolean,
    onAccountClick: (AccountUi) -> Unit,
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            accounts.forEach { item ->
                key("archived_${item.account.id}") {
                    SpacerVer(height = 12.dp)
                    AccountCard(
                        account = item.account,
                        balance = item.balance,
                        balanceBaseCurrency = item.balanceBaseCurrency
                    ) {
                        onAccountClick(item.account)
                    }
                }
            }
        }
    }
}


// region Preview
private var previewExpanded = false

@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        previewExpanded = true
        Column {
            ArchivedAccounts(
                archived = AccountListItemUi.Archived(
                    accHolders = listOf(
                        AccountListItemUi.AccountWithBalance(
                            account = dummyAccountUi("Account 1"),
                            balance = dummyValueUi("1,000.00", "BGN"),
                            balanceBaseCurrency = dummyValueUi("500")
                        ),
                        AccountListItemUi.AccountWithBalance(
                            account = dummyAccountUi("Account 2", color = Blue, excluded = true),
                            balance = dummyValueUi("0.00"),
                            balanceBaseCurrency = null
                        ),
                        AccountListItemUi.AccountWithBalance(
                            account = dummyAccountUi("Account 3", color = Red),
                            balance = dummyValueUi("4,320.50"),
                            balanceBaseCurrency = null
                        ),
                    ),
                    accountsCount = 3,
                ),
                onAccountClick = {}
            )
        }
    }
}
// endregion