package com.ivy.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.AccountData
import com.ivy.base.UiText
import com.ivy.data.AccountOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.IvyPreview
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.ItemStatistic
import com.ivy.screens.Main
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.utils.clickableNoIndication
import com.ivy.wallet.utils.horizontalSwipeListener

@Composable
fun BoxWithConstraintsScope.AccountsTab(screen: Main) {
    val viewModel: AccountsViewModel = viewModel()
    val state by viewModel.state().collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: AccountState = AccountState(),
    onEventHandler: (AccountsEvent) -> Unit = {}
) {
    val nav = navigation()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .horizontalSwipeListener(
                sensitivity = 200,
                onSwipeLeft = {
//                    ivyContext.selectMainTab(com.ivy.base.MainTab.HOME)
                },
                onSwipeRight = {
//                    ivyContext.selectMainTab(com.ivy.base.MainTab.HOME)
                }
            ),
    ) {

        item {
            Spacer(Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))

                Column {
                    Text(
                        text = stringResource(R.string.accounts),
                        style = UI.typo.b1.style(
                            color = UI.colors.pureInverse,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = state.totalBalanceWithExcludedText.asString(),
                        style = UI.typo.nB2.style(
                            color = Gray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.weight(1f))

                ReorderButton {
                    onEventHandler.invoke(AccountsEvent.OnReorderModalVisible(reorderVisible = true))
                }

                Spacer(Modifier.width(24.dp))
            }

            Spacer(Modifier.height(16.dp))
        }

        items(state.accountsData) {
            AccountCard(
                baseCurrency = state.baseCurrency,
                accountData = it,
                onBalanceClick = {
                    nav.navigateTo(
                        ItemStatistic(
                            accountId = it.account.id,
                            categoryId = null
                        )
                    )
                },
                onLongClick = {
                    onEventHandler.invoke(AccountsEvent.OnReorderModalVisible(reorderVisible = true))
                }
            ) {
                nav.navigateTo(
                    ItemStatistic(
                        accountId = it.account.id,
                        categoryId = null
                    )
                )
            }
        }

        item {
            Spacer(Modifier.height(150.dp))     //scroll hack
        }
    }

    ReorderModalSingleType(
        visible = state.reorderVisible,
        initialItems = state.accountsData,
        dismiss = {
            onEventHandler.invoke(AccountsEvent.OnReorderModalVisible(reorderVisible = false))
        },
        onReordered = {
            onEventHandler.invoke(AccountsEvent.OnReorder(reorderedList = it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.account.name,
            style = UI.typo.b1.style(
                color = item.account.color.toComposeColor(),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun AccountCard(
    baseCurrency: String,
    accountData: AccountData,
    onBalanceClick: () -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val account = accountData.account
    val contrastColor = findContrastTextColor(account.color.toComposeColor())

    Spacer(Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .clickable(
                onClick = onClick
            )
    ) {
        val currency = account.currency ?: baseCurrency

        AccountHeader(
            accountData = accountData,
            currency = currency,
            baseCurrency = baseCurrency,
            contrastColor = contrastColor,

            onBalanceClick = onBalanceClick
        )

        Spacer(Modifier.height(12.dp))

        IncomeExpensesRow(
            currency = currency,
            incomeLabel = stringResource(R.string.month_income),
            income = accountData.monthlyIncome,
            expensesLabel = stringResource(R.string.month_expenses),
            expenses = accountData.monthlyExpenses
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun AccountHeader(
    accountData: AccountData,
    currency: String,
    baseCurrency: String,
    contrastColor: Color,

    onBalanceClick: () -> Unit
) {
    val account = accountData.account

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(account.color.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = account.icon,
                defaultIcon = R.drawable.ic_custom_account_s,
                tint = contrastColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = account.name,
                style = UI.typo.b1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (!account.includeInBalance) {
                Spacer(Modifier.width(8.dp))

                Text(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 4.dp),
                    text = stringResource(R.string.excluded),
                    style = UI.typo.c.style(
                        color = account.color.toComposeColor().dynamicContrast()
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickableNoIndication {
                    onBalanceClick()
                },
            decimalPaddingTop = 7.dp,
            spacerDecimal = 6.dp,
            textColor = contrastColor,
            currency = currency,
            balance = accountData.balance,

            integerFontSize = 30.sp,
            decimalFontSize = 18.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        if (currency != baseCurrency && accountData.balanceBaseCurrency != null) {
            BalanceRowMini(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickableNoIndication {
                        onBalanceClick()
                    }
                    .testTag("baseCurrencyEquivalent"),
                textColor = account.color.toComposeColor().dynamicContrast(),
                currency = baseCurrency,
                balance = accountData.balanceBaseCurrency!!,
                currencyUpfront = false
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun PreviewAccountsTab() {
    IvyPreview {
        val state = AccountState(
            baseCurrency = "BGN",
            accountsData = listOf(
                AccountData(
                    account = AccountOld("Phyre", color = Green.toArgb()),
                    balance = 2125.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                AccountData(
                    account = AccountOld("DSK", color = GreenLight.toArgb()),
                    balance = 12125.21,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                AccountData(
                    account = AccountOld(
                        "Revolut",
                        color = IvyDark.toArgb(),
                        currency = "USD",
                        icon = "revolut",
                        includeInBalance = false
                    ),
                    balance = 1200.0,
                    balanceBaseCurrency = 1979.64,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 1000.30
                ),
                AccountData(
                    account = AccountOld(
                        "Cash",
                        color = GreenDark.toArgb(),
                        icon = "cash"
                    ),
                    balance = 820.0,
                    balanceBaseCurrency = null,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),
            ),
            totalBalanceWithExcluded = 25.54,
            totalBalanceWithExcludedText = UiText.StringResource(
                R.string.total, "BGN", "25.54"
            )
        )

        UI(state = state)
    }
}
