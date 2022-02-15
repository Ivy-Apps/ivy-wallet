package com.ivy.wallet.ui.theme.transaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.formatNicely
import com.ivy.wallet.base.isNotNullOrBlank
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.ui.ItemStatistic
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.getCustomIconIdS
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import java.time.LocalDateTime


@Composable
fun LazyItemScope.TransactionCard(
    baseCurrency: String,
    categories: List<Category>,
    accounts: List<Account>,
    transaction: Transaction,

    onPayOrGet: (Transaction) -> Unit,

    onClick: (Transaction) -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Shapes.rounded16)
            .clickable {
                if (accounts.find { it.id == transaction.accountId } != null) {
                    onClick(transaction)
                }
            }
            .background(IvyTheme.colors.medium, Shapes.rounded16)
            .testTag("transaction_card")
    ) {
        val transactionCurrency = accounts.find { it.id == transaction.accountId }?.currency
            ?: baseCurrency

        Spacer(Modifier.height(20.dp))

        TransactionHeaderRow(
            transaction = transaction,
            categories = categories,
            accounts = accounts
        )

        if (transaction.dueDate != null) {
            Spacer(Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "DUE ON ${transaction.dueDate.formatNicely()}".uppercase(),
                style = UI.typo.nC.style(
                    color = if (transaction.dueDate.isAfter(timeNowUTC()))
                        Orange else IvyTheme.colors.gray,
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
                    color = IvyTheme.colors.pureInverse
                )
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
            amount = transaction.amount
        )

        if (transaction.dueDate != null && transaction.dateTime == null) {
            //Pay/Get button
            Spacer(Modifier.height(16.dp))

            val isExpense = transaction.type == TransactionType.EXPENSE
            IvyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = if (isExpense) "Pay" else "Get",
                wrapContentMode = false,
                backgroundGradient = if (isExpense) gradientExpenses() else GradientGreen,
                textStyle = UI.typo.b2.style(
                    color = if (isExpense) IvyTheme.colors.pure else White,
                    fontWeight = FontWeight.Bold
                )
            ) {
                onPayOrGet(transaction)
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun TransactionHeaderRow(
    transaction: Transaction,
    categories: List<Category>,
    accounts: List<Account>
) {
    val nav = navigation()

    if (transaction.type == TransactionType.TRANSFER) {
        TransferHeader(
            accounts = accounts,
            transaction = transaction
        )
    } else {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val category =
                transaction.smartCategoryId()
                    ?.let { targetId -> categories.find { it.id == targetId } }
            if (category != null) {
                IvyButton(
                    text = category.name,
                    backgroundGradient = Gradient.solid(category.color.toComposeColor()),
                    hasGlow = false,
                    iconTint = findContrastTextColor(category.color.toComposeColor()),
                    iconStart = getCustomIconIdS(
                        iconName = category.icon,
                        defaultIcon = R.drawable.ic_custom_category_s
                    ),
                    textStyle = UI.typo.c.style(
                        color = findContrastTextColor(category.color.toComposeColor()),
                        fontWeight = FontWeight.ExtraBold
                    ),
                    padding = 8.dp,
                    iconEdgePadding = 10.dp
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

            val account = accounts.find { it.id == transaction.accountId }
            //TODO: Rework that by using dedicated component for "Account"
            IvyButton(
                backgroundGradient = Gradient.solid(IvyTheme.colors.pure),
                hasGlow = false,
                iconTint = IvyTheme.colors.pureInverse,
                text = account?.name ?: "deleted",
                iconStart = getCustomIconIdS(
                    iconName = account?.icon,
                    defaultIcon = R.drawable.ic_custom_account_s
                ),
                textStyle = UI.typo.c.style(
                    color = IvyTheme.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                padding = 8.dp,
                iconEdgePadding = 10.dp
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
private fun TransferHeader(
    accounts: List<Account>,
    transaction: Transaction
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(IvyTheme.colors.pure, Shapes.roundedFull),
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
            text = account?.name ?: "null",
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = IvyTheme.colors.pureInverse
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
            text = toAccount?.name ?: "null",
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = IvyTheme.colors.pureInverse
            )
        )

        Spacer(Modifier.width(20.dp))
    }
}

@Composable
fun TypeAmountCurrency(
    transactionType: TransactionType,
    dueDate: LocalDateTime?,
    currency: String,
    amount: Double
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val style = when (transactionType) {
            TransactionType.INCOME -> {
                AmountTypeStyle(
                    icon = R.drawable.ic_income,
                    gradient = GradientGreen,
                    iconTint = White,
                    textColor = Green
                )
            }
            TransactionType.EXPENSE -> {
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
                            textColor = IvyTheme.colors.pureInverse
                        )
                    }
                }
            }
            TransactionType.TRANSFER -> {
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
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash")
            val food = Category(name = "Food")

            item {
                TransactionCard(
                    baseCurrency = "BGN",
                    categories = listOf(food),
                    accounts = listOf(cash),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id,
                        amount = 250.75,
                        dueDate = timeNowUTC().plusDays(5),
                        dateTime = null,
                        type = TransactionType.EXPENSE,
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
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", color = Green.toArgb())
            val food = Category(name = "Rent", color = Green.toArgb())

            item {
                TransactionCard(
                    baseCurrency = "BGN",
                    categories = listOf(food),
                    accounts = listOf(cash),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Rent",
                        categoryId = food.id,
                        amount = 500.0,
                        dueDate = timeNowUTC().minusDays(5),
                        dateTime = null,
                        type = TransactionType.EXPENSE
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
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", color = Green.toArgb())
            val food = Category(
                name = "Bitovi",
                color = Orange.toArgb(),
                icon = "groceries"
            )

            item {
                TransactionCard(
                    baseCurrency = "BGN",
                    categories = listOf(food),
                    accounts = listOf(cash),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Близкия магазин",
                        categoryId = food.id,
                        amount = 32.51,
                        dateTime = timeNowUTC(),
                        type = TransactionType.EXPENSE
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
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "DSK Bank", color = Green.toArgb())
            val category = Category(name = "Salary", color = GreenDark.toArgb())

            item {
                TransactionCard(
                    baseCurrency = "BGN",
                    categories = listOf(category),
                    accounts = listOf(cash),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Qredo Salary May",
                        categoryId = category.id,
                        amount = 8049.70,
                        dateTime = timeNowUTC(),
                        type = TransactionType.INCOME
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
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val acc1 = Account(name = "DSK Bank", color = Green.toArgb(), icon = "bank")
            val acc2 = Account(name = "Revolut", color = IvyDark.toArgb(), icon = "revolut")

            item {
                TransactionCard(
                    baseCurrency = "BGN",
                    categories = emptyList(),
                    accounts = listOf(acc1, acc2),
                    transaction = Transaction(
                        accountId = acc1.id,
                        toAccountId = acc2.id,
                        title = "Top-up revolut",
                        amount = 1000.0,
                        dateTime = timeNowUTC(),
                        type = TransactionType.TRANSFER
                    ),
                    onPayOrGet = {},
                ) {

                }
            }
        }
    }
}