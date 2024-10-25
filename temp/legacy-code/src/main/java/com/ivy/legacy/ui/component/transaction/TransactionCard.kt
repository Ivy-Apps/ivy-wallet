package com.ivy.legacy.ui.component.transaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.legacy.LegacyTag
import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.design.api.LocalTimeConverter
import com.ivy.design.api.LocalTimeFormatter
import com.ivy.design.api.LocalTimeProvider
import com.ivy.design.l0_system.BlueLight
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.utils.capitalizeLocal
import com.ivy.legacy.utils.dateNowUTC
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.navigation.Navigation
import com.ivy.navigation.TransactionsScreen
import com.ivy.navigation.navigation
import com.ivy.ui.R
import com.ivy.ui.time.TimeFormatter
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.GradientOrangeRevert
import com.ivy.wallet.ui.theme.GradientRed
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.GreenDark
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.gradientExpenses
import com.ivy.wallet.ui.theme.toComposeColor
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Suppress("CyclomaticComplexMethod", "LongMethod")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun TransactionCard(
    baseData: AppBaseData,
    transaction: Transaction,
    shouldShowAccountSpecificColorInTransactions: Boolean,
    onPayOrGet: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onSkipTransaction: (Transaction) -> Unit = {},
    onClick: (Transaction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .clip(UI.shapes.r4)
            .clickable {
                if (baseData.accounts.find { it.id == transaction.accountId } != null) {
                    onClick(transaction)
                }
            }
            .background(UI.colors.medium, UI.shapes.r4)
            .testTag("transaction_card")
    ) {
        // TODO: Optimize this
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
            accounts = baseData.accounts,
            shouldShowAccountSpecificColorInTransactions = shouldShowAccountSpecificColorInTransactions
        )

        if (transaction.dueDate != null) {
            Spacer(Modifier.height(12.dp))
            val timeFormatter = LocalTimeFormatter.current
            val timeProvider = LocalTimeProvider.current
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(
                    R.string.due_on,
                    with(timeFormatter) {
                        transaction.dueDate!!.formatLocal(
                            TimeFormatter.Style.DateOnly(
                                includeWeekDay = true
                            )
                        )
                    }
                ).uppercase(),
                style = UI.typo.nC.style(
                    color = if (transaction.dueDate!!.isAfter(timeProvider.utcNow())) {
                        Orange
                    } else {
                        UI.colors.gray
                    },
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

        val description = getTransactionDescription(transaction)
        if (!description.isNullOrBlank()) {
            Spacer(Modifier.height(if (transaction.title.isNotNullOrBlank()) 4.dp else 8.dp))
            Text(
                text = description,
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
            dueDate = with(LocalTimeConverter.current) {
                transaction.dueDate?.toLocalDateTime()
            },
            currency = transactionCurrency,
            amount = transaction.amount.toDouble()
        )

        if (transaction.type == TransactionType.TRANSFER && toAccountCurrency != transactionCurrency) {
            Text(
                modifier = Modifier.padding(start = 68.dp),
                text = "${
                    transaction.toAmount.toDouble()
                        .format(IvyCurrency.getDecimalPlaces(toAccountCurrency))
                } $toAccountCurrency",
                style = UI.typo.nB2.style(
                    color = Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        if (transaction.dueDate != null && transaction.dateTime == null) {
            // Pay/Get button
            Spacer(Modifier.height(16.dp))
            val isExpense = transaction.type == TransactionType.EXPENSE
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

        if (transaction.tags.isNotEmpty()) {
            TransactionTags(transaction.tags)
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ColumnScope.TransactionTags(tags: ImmutableList<LegacyTag>) {
    Spacer(Modifier.height(12.dp))

    LazyRow(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        item {
            // Tag Text
            Text(
                text = "Tags:",
                style = UI.typo.nC.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        items(tags, key = { it.id }) { tag ->
            Text(
                text = "#${tag.name}",
                style = UI.typo.nC.style(
                    color = BlueLight,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionHeaderRow(
    transaction: Transaction,
    categories: List<Category>,
    accounts: List<Account>,
    shouldShowAccountSpecificColorInTransactions: Boolean,
) {
    val nav = navigation()

    val category = category(
        categoryId = transaction.categoryId,
        categories = categories
    )

    if (transaction.type == TransactionType.TRANSFER) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            if (category != null) {
                CategoryBadgeDisplay(category, nav)
                Spacer(modifier = Modifier.height(8.dp))
            }
            TransferHeader(
                accounts = accounts,
                transaction = transaction,
                shouldShowAccountSpecificColorInTransactions = shouldShowAccountSpecificColorInTransactions
            )
        }
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (category != null) {
                CategoryBadgeDisplay(category, nav)
            }

            val account = account(
                accountId = transaction.accountId,
                accounts = accounts
            )

            val accountBackgroundColor = if (shouldShowAccountSpecificColorInTransactions) {
                account?.color?.toComposeColor() ?: UI.colors.pure
            } else {
                UI.colors.pure
            }

            TransactionBadge(
                text = account?.name ?: stringResource(R.string.deleted),
                backgroundColor = accountBackgroundColor,
                icon = account?.icon,
                defaultIcon = R.drawable.ic_custom_account_s
            ) {
                account?.let {
                    nav.navigateTo(
                        TransactionsScreen(
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
fun CategoryBadgeDisplay(
    category: Category,
    nav: Navigation,
) {
    TransactionBadge(
        text = category.name.value,
        backgroundColor = category.color.value.toComposeColor(),
        icon = category.icon?.id,
        defaultIcon = R.drawable.ic_custom_category_s
    ) {
        // Navigation logic
        nav.navigateTo(
            TransactionsScreen(
                accountId = null,
                categoryId = category.id.value
            )
        )
    }
}

@Composable
private fun getTransactionDescription(transaction: Transaction): String? {
    val paidFor = with(LocalTimeConverter.current) {
        transaction.paidFor?.toLocalDateTime()
    }
    return when {
        transaction.description.isNotNullOrBlank() -> transaction.description!!
        transaction.recurringRuleId != null &&
                transaction.dueDate == null &&
                paidFor != null -> {
            stringResource(
                R.string.bill_paid,
                paidFor.month.name.lowercase().capitalizeLocal(),
                paidFor.year.toString()
            )
        }

        else -> null
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
            }
            .padding(end = 10.dp),
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

private const val TransferHeaderGradientThreshold = 0.35f

@Composable
private fun TransferHeader(
    accounts: List<Account>,
    transaction: Transaction,
    shouldShowAccountSpecificColorInTransactions: Boolean
) {
    val account = remember(accounts, transaction) {
        accounts.find { transaction.accountId == it.id }
    }
    val toAccount = remember(accounts, transaction) {
        accounts.find { transaction.toAccountId == it.id }
    }

    Row(
        modifier = Modifier
            .then(
                if (shouldShowAccountSpecificColorInTransactions && account != null && toAccount != null) {
                    Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                0f to account.color.toComposeColor(),
                                (TransferHeaderGradientThreshold) to account.color.toComposeColor(),
                                (1f - TransferHeaderGradientThreshold) to toAccount.color.toComposeColor(),
                                1f to toAccount.color.toComposeColor()
                            ),
                            shape = UI.shapes.rFull
                        )
                } else {
                    Modifier.background(UI.colors.pure, UI.shapes.rFull)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        val accountContrastColor =
            if (shouldShowAccountSpecificColorInTransactions && account != null) {
                findContrastTextColor(account.color.toComposeColor())
            } else {
                UI.colors.pureInverse
            }

        ItemIconSDefaultIcon(
            iconName = account?.icon,
            defaultIcon = R.drawable.ic_custom_account_s,
            tint = accountContrastColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            // used toString() in case of null
            text = account?.name.toString(),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = accountContrastColor
            )
        )

        Spacer(Modifier.width(12.dp))

        IvyIcon(icon = R.drawable.ic_arrow_right, tint = accountContrastColor)

        Spacer(Modifier.width(12.dp))

        val toAccountContrastColor =
            if (shouldShowAccountSpecificColorInTransactions && toAccount != null) {
                findContrastTextColor(toAccount.color.toComposeColor())
            } else {
                UI.colors.pureInverse
            }

        ItemIconSDefaultIcon(
            iconName = toAccount?.icon,
            defaultIcon = R.drawable.ic_custom_account_s,
            tint = toAccountContrastColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            // used toString() in case of null
            text = toAccount?.name.toString(),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = toAccountContrastColor
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
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("type_amount_currency"),
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
                        // Upcoming Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_expense,
                            gradient = GradientOrangeRevert,
                            iconTint = White,
                            textColor = Orange
                        )
                    }

                    dueDate != null && dueDate.isBefore(dateNowUTC().atStartOfDay()) -> {
                        // Overdue Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_overdue,
                            gradient = GradientRed,
                            iconTint = White,
                            textColor = Red
                        )
                    }

                    else -> {
                        // Normal Expense
                        AmountTypeStyle(
                            icon = R.drawable.ic_expense,
                            gradient = Gradient.black(),
                            iconTint = White,
                            textColor = UI.colors.pureInverse
                        )
                    }
                }
            }

            TransactionType.TRANSFER -> {
                // Transfer
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", Green.toArgb())
            val food = Category(
                name = NotBlankTrimmedString.unsafe("Food"),
                color = ColorInt(Blue.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(food),
                        accounts = persistentListOf(cash)
                    ),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id.value,
                        amount = 250.75.toBigDecimal(),
                        dueDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        dateTime = null,
                        type = TransactionType.EXPENSE,
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
                    onPayOrGet = {},
                ) {
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewUpcomingExpenseBadgeSecondRow() {
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", Green.toArgb())
            val food = Category(
                name = NotBlankTrimmedString.unsafe("Food-Travel-Entertaiment-Food"),
                color = ColorInt(Blue.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(food),
                        accounts = persistentListOf(cash)
                    ),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id.value,
                        amount = 250.75.toBigDecimal(),
                        dueDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        dateTime = null,
                        type = TransactionType.EXPENSE,
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", color = Green.toArgb())
            val food = Category(
                name = NotBlankTrimmedString.unsafe("Rent"),
                color = ColorInt(Green.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(food),
                        accounts = persistentListOf(cash)
                    ),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Rent",
                        categoryId = food.id.value,
                        amount = 500.0.toBigDecimal(),
                        dueDate = timeNowUTC().minusDays(5).toInstant(ZoneOffset.UTC),
                        dateTime = null,
                        type = TransactionType.EXPENSE
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash", color = Green.toArgb())
            val food = Category(
                name = NotBlankTrimmedString.unsafe("Bitovi"),
                color = ColorInt(Orange.toArgb()),
                icon = IconAsset.unsafe("groceries"),
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(food),
                        accounts = persistentListOf(cash)
                    ),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Близкия магазин",
                        categoryId = food.id.value,
                        amount = 32.51.toBigDecimal(),
                        dateTime = timeNowUTC().toInstant(ZoneOffset.UTC),
                        type = TransactionType.EXPENSE
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "DSK Bank", color = Green.toArgb())
            val category = Category(
                name = NotBlankTrimmedString.unsafe("Salary"),
                color = ColorInt(GreenDark.toArgb()),
                icon = null,
                id = CategoryId(UUID.randomUUID()),
                orderNum = 0.0,
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(category),
                        accounts = persistentListOf(cash)
                    ),
                    transaction = Transaction(
                        accountId = cash.id,
                        title = "Qredo Salary May",
                        categoryId = category.id.value,
                        amount = 8049.70.toBigDecimal(),
                        dateTime = timeNowUTC().toInstant(ZoneOffset.UTC),
                        type = TransactionType.INCOME
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val acc1 = Account(name = "DSK Bank", color = Green.toArgb(), icon = "bank")
            val acc2 = Account(name = "Revolut", color = IvyDark.toArgb(), icon = "revolut")

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(),
                        accounts = persistentListOf(acc1, acc2)
                    ),
                    transaction = Transaction(
                        accountId = acc1.id,
                        toAccountId = acc2.id,
                        title = "Top-up revolut",
                        amount = 1000.0.toBigDecimal(),
                        dateTime = timeNowUTC().toInstant(ZoneOffset.UTC),
                        type = TransactionType.TRANSFER
                    ),
                    shouldShowAccountSpecificColorInTransactions = false,
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
    IvyWalletPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val acc1 = Account(name = "DSK Bank", color = Green.toArgb(), icon = "bank")
            val acc2 = Account(
                name = "Revolut",
                currency = "EUR",
                color = IvyDark.toArgb(),
                icon = "revolut"
            )

            item {
                TransactionCard(
                    baseData = AppBaseData(
                        baseCurrency = "BGN",
                        categories = persistentListOf(),
                        accounts = persistentListOf(acc1, acc2)
                    ),
                    transaction = Transaction(
                        accountId = acc1.id,
                        toAccountId = acc2.id,
                        title = "Top-up revolut",
                        amount = 1000.0.toBigDecimal(),
                        toAmount = 510.toBigDecimal(),
                        dateTime = timeNowUTC().toInstant(ZoneOffset.UTC),
                        type = TransactionType.TRANSFER
                    ),
                    shouldShowAccountSpecificColorInTransactions = true,
                    onPayOrGet = {},
                ) {
                }
            }
        }
    }
}
