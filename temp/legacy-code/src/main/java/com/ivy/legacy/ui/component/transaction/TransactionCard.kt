package com.ivy.legacy.ui.component.transaction

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
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
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.legacy.utils.dateNowUTC
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.rememberInteractionSource
import com.ivy.legacy.utils.springBounce
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
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Compact
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1RowScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Suppress("CyclomaticComplexMethod")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun TransactionCard(
    baseData: AppBaseData,
    transaction: Transaction,
    onPayOrGet: (Transaction) -> Unit,
    compactModeEnabled: Boolean,
    onClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onSkipTransaction: (Transaction) -> Unit = {},
) {
    /**
     * State for tracking whether the buttons are expanded
     * When the skip and pay/get buttons are to be shown in compact mode
     * */
    var expanded by rememberSaveable { mutableStateOf(false) }

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

        Spacer(
            Modifier.height(
                if (compactModeEnabled) 4.dp else 20.dp
            )
        )

        if (!compactModeEnabled) {

            TransactionHeaderRow(
                transaction = transaction,
                categories = baseData.categories,
                accounts = baseData.accounts,
            )
            DueDate(transaction, false)
            Title(
                title = transaction.title,
                dueDate = transaction.dueDate,
                compactModeEnabled = false
            )
            Description(transaction)
        }

        if (transaction.dueDate != null || compactModeEnabled) {
            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(16.dp))
        }

        TitleAndTypeAmountCurrencyRow(
            transaction = transaction,
            transactionCurrency = transactionCurrency,
            toAccountCurrency = toAccountCurrency,
            compactModeEnabled = compactModeEnabled
        )

        if (!compactModeEnabled) {
            PayOrGetButtons(
                transaction = transaction,
                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction,
                compactModeEnabled = false
            )
            if (transaction.tags.isNotEmpty()) {
                TransactionTags(transaction.tags)
            }
        } else {
            PayOrGetButtonsExpandable(
                expanded = expanded,
                transaction = transaction,
                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction,
                toggleExpanded = { expanded = expanded.not() }
            )
        }

        Spacer(
            Modifier.height(
                if (compactModeEnabled) 16.dp else 20.dp
            )
        )
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
                transaction = transaction
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

            TransactionBadge(
                text = account?.name ?: stringResource(R.string.deleted),
                backgroundColor = UI.colors.pure,
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
                // Todo(Due transaction description: This needs to be set depending on whether it is income or expense)
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

@Composable
private fun TransferHeader(
    accounts: List<Account>,
    transaction: Transaction
) {
    Row(
        modifier = Modifier
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
    transactionType: TransactionType,
    dueDate: LocalDateTime?,
    currency: String,
    amount: Double,
    modifier: Modifier = Modifier,
    compactModeEnabled: Boolean = false,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = remember { configuration.screenWidthDp.dp }

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

    Row(
        modifier = modifier.testTag("type_amount_currency"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyIcon(
            modifier = Modifier
                .background(style.gradient.asHorizontalBrush(), CircleShape),
            icon = style.icon,
            tint = style.iconTint
        )

        Spacer(
            Modifier.width(
                if (compactModeEnabled) 8.dp else 12.dp
            )
        )

        /**
         * Hacky way -> Needs to be worked upon in future.
         * Added these `widthMin` modifiers as safeguard for cases when
         * the amount currency row i.e. the amount or the currency is too
         * large and overflows from the screen potentially making anything
         * that follows in this row to be hidden (title in this case)
         * This is for cases when user enters a manual currency that is too long
         * or when user enters the max value of amount (Double.MAX_VALUE).
         * */
        if (!compactModeEnabled) {
            AmountCurrencyB1RowScope(
                amount = amount,
                currency = currency,
                textColor = style.textColor,
                modifier = Modifier.widthIn(max = 0.7f * screenWidth)
            )
        } else AmountCurrencyB1Compact(
            amount = amount,
            currency = currency,
            textColor = style.textColor,
            modifier = Modifier.widthIn(max = 0.5f * screenWidth)
        )
    }
}

@Composable
private fun Title(
    title: String?,
    dueDate: Instant?,
    compactModeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (title.isNotNullOrBlank()) {
        Spacer(
            Modifier.height(
                if (dueDate != null) 8.dp else 12.dp
            )
        )

        Text(
            modifier = modifier.padding(horizontal = 24.dp),
            text = title!!,
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            ).copy(
                fontSize = if (compactModeEnabled) 18.sp else 20.sp,
                textAlign = if (compactModeEnabled) TextAlign.End else TextAlign.Start,
            ),
            // Apply max lines and ellipsis in compact mode
            maxLines = if (compactModeEnabled) 2 else Int.MAX_VALUE,
            overflow = if (compactModeEnabled) TextOverflow.Ellipsis else TextOverflow.Clip,
        )
    }
}

@Composable
private fun Description(
    transaction: Transaction
) {
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
}

@Composable
private fun DueDate(
    transaction: Transaction,
    compactModeEnabled: Boolean
) {
    if (transaction.dueDate != null) {
        Spacer(
            Modifier.height(
                if (compactModeEnabled) 12.dp else 12.dp
            )
        )

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
}

@Composable
private fun TitleAndTypeAmountCurrencyRow(
    transaction: Transaction,
    transactionCurrency: String,
    toAccountCurrency: String,
    compactModeEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = if (compactModeEnabled) 2.dp else 24.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Transaction type icon and amount
        TypeAmountCurrency(
            transactionType = transaction.type,
            dueDate = with(LocalTimeConverter.current) {
                transaction.dueDate?.toLocalDateTime()
            },
            currency = transactionCurrency,
            amount = transaction.amount.toDouble(),
            compactModeEnabled = compactModeEnabled,
        )

        // Title
        if (compactModeEnabled) {
            Title(
                title = transaction.title,
                dueDate = transaction.dueDate,
                compactModeEnabled = true,
            )
        }
    }

    // Optional different currency conversion text
    if (transaction.type == TransactionType.TRANSFER && toAccountCurrency != transactionCurrency) {
        Text(
            modifier = Modifier.padding(
                start = if (compactModeEnabled) 64.dp else 68.dp
            ),
            text = "${
                transaction.toAmount.toDouble()
                    .format(IvyCurrency.getDecimalPlaces(toAccountCurrency))
            } $toAccountCurrency",
            style = UI.typo.nB2.style(
                color = Gray,
                fontWeight = FontWeight.Normal
            ).copy(
                fontSize = if (compactModeEnabled) 14.sp else 16.sp
            )
        )
    }
}

@Composable
private fun PayOrGetButtons(
    transaction: Transaction,
    onPayOrGet: (Transaction) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
    compactModeEnabled: Boolean
) {
    // Skip and Pay/Get buttons
    if (transaction.dueDate != null && transaction.dateTime == null) {
        Spacer(
            Modifier.height(
                if (compactModeEnabled) 12.dp else 16.dp
            )
        )

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
}

// For only when compact mode enabled
@Composable
private fun PayOrGetButtonsExpandable(
    transaction: Transaction,
    onPayOrGet: (Transaction) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
    expanded: Boolean = false,
    toggleExpanded: () -> Unit = {},
) {
    val canExpand = transaction.dueDate != null && transaction.dateTime == null
    val interactionSource = rememberInteractionSource()
    val iconClick = remember {
        Modifier.clickableNoIndication(interactionSource) {
            if (canExpand) {
                toggleExpanded()
            }
        }
    }

    /**
     * Never gonna happen (just for previews) - as safeguard
     * if this scenario is ever shown to the user due to some bug
     * Hacky top padding when the drop down icon is not showing
     * But the "due on" text is to be shown
     * */
    if (!canExpand && transaction.dueDate != null) {
        Spacer(Modifier.height(8.dp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val expandIconRotation by animateFloatAsState(
            targetValue = if (expanded && canExpand) -180f else 0f,
            animationSpec = springBounce()
        )

        DueDate(transaction, true)

        Spacer(Modifier.weight(1f))

        if (canExpand) {
            IvyIcon(
                modifier = Modifier
                    .rotate(expandIconRotation)
                    .then(iconClick),
                icon = R.drawable.ic_expandarrow
            )
            Spacer(Modifier.width(24.dp))
        }
    }

    if (expanded) {
        PayOrGetButtons(
            transaction = transaction,
            onPayOrGet = onPayOrGet,
            onSkipTransaction = onSkipTransaction,
            compactModeEnabled = true
        )
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
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {},
                )
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
                        description = "This is a sample description",
                        type = TransactionType.EXPENSE,
                    ),
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
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
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
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
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
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
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
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
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewUpcomingIncome() {
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
                        dueDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        dateTime = null,
                        type = TransactionType.INCOME
                    ),
                    compactModeEnabled = false,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}
@Preview
@Composable
private fun PreviewCompactUpcomingExpense() {
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactUpcomingExpenseBadgeSecondRow() {
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
                        description = "This is a sample description",
                        type = TransactionType.EXPENSE,
                    ),
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactOverdueExpense() {
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactNormalExpense() {
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactIncome() {
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactTransfer() {
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
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompactTransfer_differentCurrency() {
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
                        title = "Top-up revolut Very long very long very lpng very lpng very lpng very long ",
                        amount = 5109876543673887654.toBigDecimal(),
                        toAmount = 5109876543673887654.toBigDecimal(),
                        dateTime = timeNowUTC().toInstant(ZoneOffset.UTC),
                        type = TransactionType.TRANSFER
                    ),
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}


@Preview
@Composable
private fun PreviewCompactUpcomingIncome() {
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
                        dueDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        dateTime = null,
                        type = TransactionType.INCOME
                    ),
                    compactModeEnabled = true,
                    onPayOrGet = {},
                    onClick = {}
                )
            }
        }
    }
}