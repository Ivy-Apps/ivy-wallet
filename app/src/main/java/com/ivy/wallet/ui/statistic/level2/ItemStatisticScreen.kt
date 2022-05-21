package com.ivy.wallet.ui.statistic.level2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsHeight
import com.ivy.design.l0_system.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.*
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.transaction.transactions
import com.ivy.wallet.ui.theme.wallet.PeriodSelector
import com.ivy.wallet.utils.*


@Composable
fun BoxWithConstraintsScope.ItemStatisticScreen(screen: ItemStatistic) {
    val viewModel: ItemStatisticViewModel = viewModel()

    val ivyContext = ivyWalletCtx()
    val nav = navigation()

    val period by viewModel.period.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val currency by viewModel.currency.collectAsState()

    val account by viewModel.account.collectAsState()
    val category by viewModel.category.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    val balance by viewModel.balance.collectAsState()
    val balanceBaseCurrency by viewModel.balanceBaseCurrency.collectAsState()
    val income by viewModel.income.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val history by viewModel.history.collectAsState()

    val upcoming by viewModel.upcoming.collectAsState()
    val upcomingExpanded by viewModel.upcomingExpanded.collectAsState()
    val upcomingIncome by viewModel.upcomingIncome.collectAsState()
    val upcomingExpenses by viewModel.upcomingExpenses.collectAsState()

    val overdue by viewModel.overdue.collectAsState()
    val overdueExpanded by viewModel.overdueExpanded.collectAsState()
    val overdueIncome by viewModel.overdueIncome.collectAsState()
    val overdueExpenses by viewModel.overdueExpenses.collectAsState()

    val initWithTransactions by viewModel.initWithTransactions.collectAsState()
    val treatTransfersAsIncomeExpense by viewModel.treatTransfersAsIncomeExpense.collectAsState()

    val view = LocalView.current
    onScreenStart {
        viewModel.start(screen)

        nav.onBackPressed[screen] = {
            setStatusBarDarkTextCompat(
                view = view,
                darkText = ivyContext.theme == Theme.LIGHT
            )
            false
        }
    }

    UI(
        period = period,
        baseCurrency = baseCurrency,
        currency = currency,

        categories = categories,
        accounts = accounts,

        account = account,
        category = category,

        balance = balance,
        balanceBaseCurrency = balanceBaseCurrency,
        income = income,
        expenses = expenses,

        initWithTransactions = initWithTransactions,
        treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense,

        history = history,

        upcoming = upcoming,
        upcomingExpanded = upcomingExpanded,
        setUpcomingExpanded = viewModel::setUpcomingExpanded,
        upcomingIncome = upcomingIncome,
        upcomingExpenses = upcomingExpenses,

        overdue = overdue,
        overdueExpanded = overdueExpanded,
        setOverdueExpanded = viewModel::setOverdueExpanded,
        overdueIncome = overdueIncome,
        overdueExpenses = overdueExpenses,


        onSetPeriod = {
            viewModel.setPeriod(
                screen = screen,
                period = it
            )
        },
        onNextMonth = {
            viewModel.nextMonth(screen)
        },
        onPreviousMonth = {
            viewModel.previousMonth(screen)
        },
        onDelete = {
            viewModel.delete(screen)
        },
        onEditCategory = viewModel::editCategory,
        onEditAccount = { acc, newBalance ->
            viewModel.editAccount(screen, acc, newBalance)
        },
        onPayOrGet = { transaction ->
            viewModel.payOrGet(screen, transaction)
        }
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    period: TimePeriod,
    baseCurrency: String,
    currency: String,

    account: Account?,
    category: Category?,

    categories: List<Category>,
    accounts: List<Account>,

    balance: Double,
    balanceBaseCurrency: Double?,
    income: Double,
    expenses: Double,

    initWithTransactions: Boolean = false,
    treatTransfersAsIncomeExpense: Boolean = false,

    history: List<TransactionHistoryItem>,

    upcomingExpanded: Boolean = true,
    setUpcomingExpanded: (Boolean) -> Unit = {},
    upcomingIncome: Double = 0.0,
    upcomingExpenses: Double = 0.0,
    upcoming: List<Transaction> = emptyList(),

    overdueExpanded: Boolean = true,
    setOverdueExpanded: (Boolean) -> Unit = {},
    overdueIncome: Double = 0.0,
    overdueExpenses: Double = 0.0,
    overdue: List<Transaction> = emptyList(),

    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSetPeriod: (TimePeriod) -> Unit,
    onEditAccount: (Account, Double) -> Unit,
    onEditCategory: (Category) -> Unit,
    onDelete: () -> Unit,
    onPayOrGet: (Transaction) -> Unit = {}
) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()
    val itemColor = (account?.color ?: category?.color)?.toComposeColor() ?: Gray

    var deleteModalVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(itemColor)
            .thenIf(!initWithTransactions)
            {
                horizontalSwipeListener(
                    sensitivity = 150,
                    onSwipeLeft = {
                        onNextMonth()
                    },
                    onSwipeRight = {
                        onPreviousMonth()
                    }
                )
            }

    ) {
        val listState = rememberLazyListState()
        val density = LocalDensity.current

        Spacer(Modifier.statusBarsHeight())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .clip(UI.shapes.r1Top)
                .background(UI.colors.pure),
            state = listState,
        ) {
            item {
                Header(
                    history = history,
                    income = income,
                    expenses = expenses,
                    currency = currency,
                    baseCurrency = baseCurrency,
                    itemColor = itemColor,
                    account = account,
                    category = category,
                    balance = balance,
                    balanceBaseCurrency = balanceBaseCurrency,
                    treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense,

                    onDelete = {
                        deleteModalVisible = true
                    },
                    onEdit = {
                        when {
                            account != null -> {
                                accountModalData = AccountModalData(
                                    account = account,
                                    baseCurrency = currency,
                                    balance = balance,
                                    autoFocusKeyboard = false
                                )
                            }
                            category != null -> {
                                categoryModalData = CategoryModalData(
                                    category = category,
                                    autoFocusKeyboard = false
                                )
                            }
                        }
                    },

                    onBalanceClick = {
                        when {
                            account != null -> {
                                accountModalData = AccountModalData(
                                    account = account,
                                    baseCurrency = currency,
                                    balance = balance,
                                    adjustBalanceMode = true,
                                    autoFocusKeyboard = false
                                )
                            }
                        }
                    },
                    showCategoryModal = {
                        categoryModalData = CategoryModalData(
                            category = category,
                            autoFocusKeyboard = false
                        )
                    },
                    showAccountModal = {
                        accountModalData = AccountModalData(
                            account = account,
                            baseCurrency = currency,
                            balance = balance,
                            adjustBalanceMode = false,
                            autoFocusKeyboard = false
                        )
                    }
                )
            }

            item {
                //Rounded corners top effect
                Box {
                    Spacer(
                        Modifier
                            .height(32.dp)
                            .fillMaxWidth()
                            .background(itemColor) //itemColor is displayed below the clip
                            .background(UI.colors.pure, UI.shapes.r1Top)
                    )

                    PeriodSelector(
                        modifier = Modifier.padding(top = 16.dp),
                        period = period,
                        onPreviousMonth = { if (!initWithTransactions) onPreviousMonth() },
                        onNextMonth = { if (!initWithTransactions) onNextMonth() },
                        onShowChoosePeriodModal = {
                            if (!initWithTransactions)
                                choosePeriodModal = ChoosePeriodModalData(
                                    period = period
                                )
                        }
                    )
                }
            }

            transactions(
                ivyContext = ivyContext,
                nav = nav,
                upcoming = upcoming,
                upcomingExpanded = upcomingExpanded,
                setUpcomingExpanded = setUpcomingExpanded,

                baseCurrency = baseCurrency,
                upcomingIncome = upcomingIncome,

                upcomingExpenses = upcomingExpenses,
                categories = categories,
                accounts = accounts,
                listState = listState,
                overdue = overdue,

                overdueExpanded = overdueExpanded,
                setOverdueExpanded = setOverdueExpanded,
                overdueIncome = overdueIncome,
                overdueExpenses = overdueExpenses,
                history = history,

                lastItemSpacer = with(density) {
                    (ivyContext.screenHeight * 0.7f).toDp()
                },
                onPayOrGet = onPayOrGet,
                emptyStateTitle = stringRes(R.string.no_transactions),

                emptyStateText = stringRes(
                    R.string.no_transactions_for_period,
                    period.toDisplayLong(ivyContext.startDayOfMonth)
                ),

                )
        }
    }

    DeleteModal(
        visible = deleteModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = if (account != null) {
            stringResource(R.string.account_confirm_deletion_description)
        } else {
            stringResource(R.string.category_confirm_deletion_description)
        },
        dismiss = { deleteModalVisible = false }
    ) {
        onDelete()
    }

    CategoryModal(
        modal = categoryModalData,
        onCreateCategory = { },
        onEditCategory = onEditCategory,
        dismiss = {
            categoryModalData = null
        }
    )

    AccountModal(
        modal = accountModalData,
        onCreateAccount = { },
        onEditAccount = onEditAccount,
        dismiss = {
            accountModalData = null
        }
    )

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        }
    ) {
        onSetPeriod(it)
    }
}

@Composable
private fun Header(
    history: List<TransactionHistoryItem>,
    currency: String,
    baseCurrency: String,
    itemColor: Color,
    account: Account?,
    category: Category?,
    balance: Double,
    balanceBaseCurrency: Double?,
    income: Double,
    expenses: Double,
    treatTransfersAsIncomeExpense: Boolean = false,

    onEdit: () -> Unit,
    onDelete: () -> Unit,

    onBalanceClick: () -> Unit,
    showCategoryModal: () -> Unit,
    showAccountModal: () -> Unit,
) {
    val contrastColor = findContrastTextColor(itemColor)

    val darkColor = isDarkColor(itemColor)
    setStatusBarDarkTextCompat(darkText = !darkColor)

    Column(
        modifier = Modifier.background(itemColor)
    ) {
        Spacer(Modifier.height(20.dp))

        ItemStatisticToolbar(
            contrastColor = contrastColor,
            onEdit = onEdit,
            onDelete = onDelete
        )

        Spacer(Modifier.height(24.dp))

        Item(
            itemColor = itemColor,
            contrastColor = contrastColor,
            account = account,
            category = category,

            showAccountModal = showAccountModal,
            showCategoryModal = showCategoryModal
        )

        BalanceRow(
            modifier = Modifier
                .padding(start = 32.dp)
                .testTag("balance")
                .clickableNoIndication {
                    onBalanceClick()
                },
            textColor = contrastColor,
            currency = currency,
            balance = balance,
            balanceAmountPrefix = if (category != null) balancePrefix(
                income = income,
                expenses = expenses
            ) else null
        )

        if (currency != baseCurrency && balanceBaseCurrency != null) {
            BalanceRowMedium(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .clickableNoIndication {
                        onBalanceClick()
                    },
                textColor = itemColor.dynamicContrast(),
                currency = baseCurrency,
                balance = balanceBaseCurrency,
                balanceAmountPrefix = if (category != null) balancePrefix(
                    income = income,
                    expenses = expenses
                ) else null
            )
        }

        Spacer(Modifier.height(20.dp))

        val nav = navigation()
        IncomeExpensesCards(
            history = history,
            currency = currency,
            income = income,
            expenses = expenses,

            hasAddButtons = true,

            itemColor = itemColor,
            incomeHeaderCardClicked = {
                if (account != null) {
                    nav.navigateTo(
                        PieChartStatistic(
                            type = TransactionType.INCOME,
                            accountList = listOf(account.id),
                            filterExcluded = false,
                            treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense
                        )
                    )
                }
            },
            expenseHeaderCardClicked = {
                if (account != null) {
                    nav.navigateTo(
                        PieChartStatistic(
                            type = TransactionType.EXPENSE,
                            accountList = listOf(account.id),
                            filterExcluded = false,
                            treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense
                        )
                    )
                }
            }
        ) { trnType ->
            nav.navigateTo(
                EditTransaction(
                    initialTransactionId = null,
                    type = trnType,
                    accountId = account?.id,
                    categoryId = category?.id
                )
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun ItemStatisticToolbar(
    contrastColor: Color,

    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val nav = navigation()
        CircleButton(
            modifier = Modifier.testTag("toolbar_close"),
            icon = R.drawable.ic_dismiss,
            borderColor = contrastColor,
            tint = contrastColor,
            backgroundColor = Transparent
        ) {
            nav.back()
        }

        Spacer(Modifier.weight(1f))

        IvyOutlinedButton(
            iconStart = R.drawable.ic_edit,
            text = stringRes(R.string.edit),
            borderColor = contrastColor,
            iconTint = contrastColor,
            textColor = contrastColor,
            solidBackground = false
        ) {
            onEdit()
        }

        Spacer(Modifier.width(16.dp))

        DeleteButton {
            onDelete()
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
fun IncomeExpensesCards(
    history: List<TransactionHistoryItem>,
    currency: String,
    income: Double,
    expenses: Double,

    hasAddButtons: Boolean,
    itemColor: Color,

    incomeHeaderCardClicked: () -> Unit = {},
    expenseHeaderCardClicked: () -> Unit = {},
    onAddTransaction: (TransactionType) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        HeaderCard(
            title = stringRes(R.string.income_uppercase),
            currencyCode = currency,
            amount = income,
            transactionCount = history
                .filterIsInstance(Transaction::class.java)
                .count { it.type == TransactionType.INCOME },
            addButtonText = if (hasAddButtons) stringResource(R.string.add_income) else null,
            isIncome = true,

            itemColor = itemColor,
            onHeaderCardClicked = { incomeHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.INCOME)
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            title = stringRes(R.string.expenses_uppercase),
            currencyCode = currency,
            amount = expenses,
            transactionCount = history
                .filterIsInstance(Transaction::class.java)
                .count { it.type == TransactionType.EXPENSE },
            addButtonText = if (hasAddButtons) stringResource(R.string.add_expense) else null,
            isIncome = false,

            itemColor = itemColor,
            onHeaderCardClicked = { expenseHeaderCardClicked() }
        ) {
            onAddTransaction(TransactionType.EXPENSE)
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun RowScope.HeaderCard(
    title: String,
    currencyCode: String,
    amount: Double,
    transactionCount: Int,

    isIncome: Boolean,
    addButtonText: String?,

    itemColor: Color,

    onHeaderCardClicked: () -> Unit = {},
    onAddClick: () -> Unit
) {
    val backgroundColor = if (isDarkColor(itemColor))
        MediumBlack.copy(alpha = 0.9f) else MediumWhite.copy(alpha = 0.9f)

    val contrastColor = findContrastTextColor(backgroundColor)

    Column(
        modifier = Modifier
            .weight(1f)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, UI.shapes.r2)
            .clickable { onHeaderCardClicked() },
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = title,
            style = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = amount.format(currencyCode),
            style = UI.typo.nB1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = IvyCurrency.fromCode(currencyCode)?.name ?: "",
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = transactionCount.toString(),
            style = UI.typo.nB1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringRes(R.string.transactions),
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(24.dp))

        if (addButtonText != null) {
            val addButtonBackground = if (isIncome) Green else contrastColor
            IvyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = addButtonText,
                shadowAlpha = 0.1f,
                backgroundGradient = Gradient.solid(addButtonBackground),
                textStyle = UI.typo.b2.style(
                    color = findContrastTextColor(addButtonBackground),
                    fontWeight = FontWeight.Bold
                ),
                wrapContentMode = false
            ) {
                onAddClick()
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun Item(
    itemColor: Color,
    contrastColor: Color,
    account: Account?,
    category: Category?,

    showCategoryModal: () -> Unit,
    showAccountModal: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp)
            .clickableNoIndication {
                when {
                    account != null -> {
                        showAccountModal()
                    }
                    category != null -> {
                        showCategoryModal()
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            account != null -> {
                ItemIconMDefaultIcon(
                    iconName = account.icon,
                    defaultIcon = R.drawable.ic_custom_account_m,
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
                            .padding(bottom = 12.dp),
                        text = stringRes(R.string.excluded),
                        style = UI.typo.c.style(
                            color = account.color.toComposeColor().dynamicContrast()
                        )
                    )
                }
            }
            category != null -> {
                ItemIconMDefaultIcon(
                    iconName = category.icon,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = contrastColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = category.name,
                    style = UI.typo.b1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
            else -> {
                //Unspecified
                ItemIconMDefaultIcon(
                    iconName = null,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = contrastColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = Constants.CATEGORY_UNSPECIFIED_NAME,
                    style = UI.typo.b1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_empty() {
    IvyWalletPreview {
        UI(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            baseCurrency = "BGN",
            currency = "BGN",

            categories = emptyList(),
            accounts = emptyList(),

            balance = 1314.578,
            balanceBaseCurrency = null,
            income = 8000.0,
            expenses = 6000.0,

            history = emptyList(),
            category = null,
            account = Account("DSK", color = GreenDark.toArgb(), icon = "pet"),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {}
        )
    }
}

@Preview
@Composable
private fun Preview_crypto() {
    IvyWalletPreview {
        UI(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            baseCurrency = "BGN",
            currency = "ADA",

            categories = emptyList(),
            accounts = emptyList(),

            balance = 1314.578,
            balanceBaseCurrency = 2879.28,
            income = 8000.0,
            expenses = 6000.0,

            history = emptyList(),
            category = null,
            account = Account(
                name = "DSK",
                color = GreenDark.toArgb(),
                icon = "pet",
                includeInBalance = false
            ),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {}
        )
    }
}