package com.ivy.wallet.ui.component.transaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.base.data.AppBaseData
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.frp.view.navigation.navigation
import com.ivy.old.component.transaction.account
import com.ivy.old.component.transaction.category
import com.ivy.screens.ItemStatistic
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import com.ivy.wallet.utils.*
import java.time.LocalDateTime


@Composable
fun TransactionCard(
    baseData: AppBaseData,

    transaction: TransactionOld,

    onPayOrGet: (TransactionOld) -> Unit,
    onSkipTransaction: (TransactionOld) -> Unit = {},

    onClick: (TransactionOld) -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .clickable {
                if (baseData.accounts.find { it.id == transaction.accountId } != null) {
                    onClick(transaction)
                }
            }
            .background(UI.colors.medium, UI.shapes.r4)
            .testTag("transaction_card")
    ) {
        //TODO: Optimize this
        val transactionCurrency =
            baseData.accounts.find { it.id == transaction.accountId }?.currency
                ?: baseData.baseCurrency

        val toAccountCurrency =
            baseData.accounts.find { it.id == transaction.toAccountId }?.currency
                ?: baseData.baseCurrency

        Spacer(Modifier.height(20.dp))

        TransactionHeaderRow(
            transaction = transaction,
            categories = baseData.categories,
            accounts = baseData.accounts
        )

        if (transaction.dueDate != null) {
            Spacer(Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(
                    R.string.due_on,
                    transaction.dueDate!!.formatNicely()
                ).uppercase(),
                style = UI.typo.nC.style(
                    color = if (transaction.dueDate!!.isAfter(timeNowUTC()))
                        Orange else UI.colors.gray,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (transaction.title.isNotNullOrBlank()) {
            Spacer(
                Modifier.height(
                    if (transaction.dueDate != null) 8.dp else 12.dp
                )
            )

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = transaction.title!!,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

        }

        if (transaction.description.isNotNullOrBlank()) {
            Spacer(
                Modifier.height(
                    if (transaction.title.isNotNullOrBlank()) 4.dp else 8.dp
                )
            )

            Text(
                text = transaction.description!!,
                modifier = Modifier.padding(horizontal = 24.dp),
                style = UI.typo.nC.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (transaction.dueDate != null) {
            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(16.dp))
        }

        TypeAmountCurrency(
            transactionType = transaction.type,
            dueDate = transaction.dueDate,
            currency = transactionCurrency,
            amount = transaction.amount.toDouble()
        )

        if (transaction.type == TrnType.TRANSFER && toAccountCurrency != transactionCurrency) {
            Text(
                modifier = Modifier.padding(start = 68.dp),
                text = "${transaction.toAmount.toDouble().format(2)} $toAccountCurrency",
                style = UI.typo.nB2.style(
                    color = Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        if (transaction.dueDate != null && transaction.dateTime == null) {
            //Pay/Get button
            Spacer(Modifier.height(16.dp))

            val isExpense = transaction.type == TrnType.EXPENSE
            Row {
                IvyButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 24.dp),
                    text = stringResource(R.string.skip),
                    wrapContentMode = false,
                    backgroundGradient = Gradient.solid(UI.colors.pure),
                    textStyle = UI.typo.b2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    onSkipTransaction(transaction)
                }

                Spacer(Modifier.width(8.dp))

                IvyButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 24.dp),
                    text = if (isExpense) stringResource(R.string.pay) else stringResource(R.string.get),
                    wrapContentMode = false,
                    backgroundGradient = if (isExpense) gradientExpenses() else GradientGreen,
                    textStyle = UI.typo.b2.style(
                        color = if (isExpense) UI.colors.pure else White,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    onPayOrGet(transaction)
                }
            }

        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun TransactionHeaderRow(
    transaction: TransactionOld,
    categories: List<CategoryOld>,
    accounts: List<AccountOld>
) {
    val nav = navigation()

    if (transaction.type == TrnType.TRANSFER) {
        TransferHeader(
            accounts = accounts,
            transaction = transaction
        )
    } else {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val category = category(
                categoryId = transaction.categoryId,
                categories = categories
            )

            if (category != null) {
                TransactionBadge(
                    text = category.name,
                    backgroundColor = category.color.toComposeColor(),
                    icon = category.icon,
                    defaultIcon = R.drawable.ic_custom_category_s
                ) {
                    nav.navigateTo(
                        ItemStatistic(
                            accountId = null,
                            categoryId = category.id
                        )
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            val account = account(
                accountId = transaction.accountId,
                accounts = accounts
            )

            TransactionBadge(
                text = account?.name ?: stringResource(R.string.deleted),
                backgroundColor = UI.colors.pure,
                icon = account?.icon,
                defaultIcon = R.drawable.ic_custom_account_s
            ) {
                account?.let {
                    nav.navigateTo(
                        ItemStatistic(
                            accountId = account.id,
                            categoryId = null
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionBadge(
    text: String,
    backgroundColor: Color,
    icon: String?,
    @DrawableRes
    defaultIcon: Int,

    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .background(backgroundColor, UI.shapes.rFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 8.dp)

        val contrastColor = findContrastTextColor(backgroundColor)

        ItemIconSDefaultIcon(
            iconName = icon,
            defaultIcon = defaultIcon,
            tint = contrastColor
        )

        SpacerHor(width = 4.dp)

        IvyText(
            text = text,
            typo = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        SpacerHor(width = 20.dp)
    }
}

@Composable
private fun TransferHeader(
    accounts: List<AccountOld>,
    transaction: TransactionOld
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(UI.colors.pure, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        val account = accounts.find { transaction.accountId == it.id }
        ItemIconSDefaultIcon(
            iconName = account?.icon,
            defaultIcon = R.drawable.ic_custom_account_s
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            // used toString() in case of null
            text = account?.name.toString(),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(12.dp))

        IvyIcon(icon = R.drawable.ic_arrow_right)

        Spacer(Modifier.width(12.dp))

        val toAccount = accounts.find { transaction.toAccountId == it.id }
        ItemIconSDefaultIcon(
            iconName = toAccount?.icon,
            defaultIcon = R.drawable.ic_custom_account_s
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            // used toString() in case of null
            text = toAccount?.name.toString(),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(20.dp))
    }
}

@Composable
fun TypeAmountCurrency(
    transactionType: TrnType,
    dueDate: LocalDateTime?,
    currency: String,
    amount: Double
) {

    Row(
        modifier = Modifier.testTag("type_amount_currency"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val style = when (transactionType) {
            TrnType.INCOME -> {
                AmountTypeStyle(
                    icon = R.drawable.ic_income,
                    gradient = GradientGreen,
                    iconTint = White,
                    textColor = Green
                )
            }
            TrnType.EXPENSE -> {
                when {
                    dueDate != null && dueDate.isAfter(timeNowUTC()) -> {
                        //Upcoming Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_expense,
                            gradient = GradientOrangeRevert,
                            iconTint = White,
                            textColor = Orange
                        )
                    }
                    dueDate != null && dueDate.isBefore(dateNowUTC().atStartOfDay()) -> {
                        //Overdue Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_overdue,
                            gradient = GradientRed,
                            iconTint = White,
                            textColor = Red
                        )
                    }
                    else -> {
                        //Normal Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_expense,
                            gradient = Gradient.black(),
                            iconTint = White,
                            textColor = UI.colors.pureInverse
                        )
                    }
                }
            }
            TrnType.TRANSFER -> {
                //Transfer
                AmountTypeStyle(
                    icon = R.drawable.ic_transfer,
                    gradient = GradientIvy,
                    iconTint = White,
                    textColor = Ivy
                )
            }
        }

        IvyIcon(
            modifier = Modifier
                .background(style.gradient.asHorizontalBrush(), CircleShape),
            icon = style.icon,
            tint = style.iconTint
        )

        Spacer(Modifier.width(12.dp))

        AmountCurrencyB1(
            amount = amount,
            currency = currency,
            textColor = style.textColor
        )

        Spacer(Modifier.width(24.dp))
    }
}

private data class AmountTypeStyle(
    @DrawableRes val icon: Int,
    val gradient: Gradient,
    val iconTint: Color,
    val textColor: Color
)

@Preview
@Composable
private fun PreviewUpcomingExpense() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = AccountOld(name = "Cash", color = Green.toArgb())
            val food = CategoryOld(name = "Food", color = Green.toArgb())

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(food),
                        accounts = listOf(cash)
                    ),
                    transaction = TransactionOld(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id,
                        amount = 250.75.toBigDecimal(),
                        dueDate = timeNowUTC().plusDays(5),
                        dateTime = null,
                        type = TrnType.EXPENSE,
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewOverdueExpense() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = AccountOld(name = "Cash", color = Green.toArgb())
            val food = CategoryOld(name = "Rent", color = Green.toArgb())

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(food),
                        accounts = listOf(cash)
                    ),
                    transaction = TransactionOld(
                        accountId = cash.id,
                        title = "Rent",
                        categoryId = food.id,
                        amount = 500.0.toBigDecimal(),
                        dueDate = timeNowUTC().minusDays(5),
                        dateTime = null,
                        type = TrnType.EXPENSE
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNormalExpense() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = AccountOld(name = "Cash", color = Green.toArgb())
            val food = CategoryOld(
                name = "Bitovi",
                color = Orange.toArgb(),
                icon = "groceries"
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(food),
                        accounts = listOf(cash)
                    ),
                    transaction = TransactionOld(
                        accountId = cash.id,
                        title = "Близкия магазин",
                        categoryId = food.id,
                        amount = 32.51.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        type = TrnType.EXPENSE
                    ),
                    onPayOrGet = {},
                ) {
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewIncome() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = AccountOld(name = "DSK Bank", color = Green.toArgb())
            val category = CategoryOld(name = "Salary", color = GreenDark.toArgb())

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(category),
                        accounts = listOf(cash)
                    ),
                    transaction = TransactionOld(
                        accountId = cash.id,
                        title = "Qredo Salary May",
                        categoryId = category.id,
                        amount = 8049.70.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        type = TrnType.INCOME
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransfer() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val acc1 = AccountOld(name = "DSK Bank", color = Green.toArgb(), icon = "bank")
            val acc2 = AccountOld(name = "Revolut", color = IvyDark.toArgb(), icon = "revolut")

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(),
                        accounts = listOf(acc1, acc2)
                    ),
                    transaction = TransactionOld(
                        accountId = acc1.id,
                        toAccountId = acc2.id,
                        title = "Top-up revolut",
                        amount = 1000.0.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        type = TrnType.TRANSFER
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewTransfer_differentCurrency() {
    com.ivy.core.ui.temp.Preview {
        LazyColumn(Modifier.fillMaxSize()) {
            val acc1 = AccountOld(name = "DSK Bank", color = Green.toArgb(), icon = "bank")
            val acc2 = AccountOld(
                name = "Revolut",
                currency = "EUR",
                color = IvyDark.toArgb(),
                icon = "revolut"
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = listOf(),
                        accounts = listOf(acc1, acc2)
                    ),
                    transaction = TransactionOld(
                        accountId = acc1.id,
                        toAccountId = acc2.id,
                        title = "Top-up revolut",
                        amount = 1000.0.toBigDecimal(),
                        toAmount = 510.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        type = TrnType.TRANSFER
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}