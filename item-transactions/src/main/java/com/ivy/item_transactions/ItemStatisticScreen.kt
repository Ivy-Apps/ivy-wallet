package com.ivy.item_transactions

import androidx.compose.foundation.background
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
import com.ivy.base.Constants
import com.ivy.base.R
import com.ivy.base.data.AppBaseData
import com.ivy.base.data.DueSection
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.pure.IncomeExpensePair
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.IvyPreview
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.old.IncomeExpensesCards
import com.ivy.old.ItemStatisticToolbar
import com.ivy.screens.EditTransaction
import com.ivy.screens.ItemStatistic
import com.ivy.screens.PieChartStatistic
import com.ivy.wallet.ui.component.transaction.transactions
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.BalanceRowMedium
import com.ivy.wallet.ui.theme.components.ItemIconMDefaultIcon
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.wallet.PeriodSelector
import com.ivy.wallet.utils.*
import java.math.BigDecimal
import java.util.*


@Composable
fun BoxWithConstraintsScope.ItemStatisticScreen(screen: ItemStatistic) {
    val viewModel: ItemStatisticViewModel = viewModel()

    val nav = navigation()

    val period by viewModel.period.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val currency by viewModel.currency.collectAsState()

    val account by viewModel.account.collectAsState()
    val category by viewModel.category.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val isCategoryParentCategory by viewModel.isParentCategory.collectAsState()
    val parentCategoryList by viewModel.parentCategoryList.collectAsState()
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
                darkText = true
            )
            false
        }
    }

    UI(
        period = period,
        baseCurrency = baseCurrency,
        currency = currency,

        categories = categories,
        isCategoryParentCategory = isCategoryParentCategory,
        parentCategoryList = parentCategoryList,
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
        },
        onSkipTransaction = { transaction ->
            viewModel.skipTransaction(screen, transaction)
        },
        onSkipAllTransactions = { transactions ->
            viewModel.skipTransactions(screen, transactions)
        }
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    period: TimePeriod,
    baseCurrency: String,
    currency: String,

    account: AccountOld?,
    category: CategoryOld?,

    categories: List<CategoryOld>,
    isCategoryParentCategory: Boolean = true,
    parentCategoryList: List<CategoryOld> = emptyList(),
    accounts: List<AccountOld>,

    balance: Double,
    balanceBaseCurrency: Double?,
    income: Double,
    expenses: Double,

    initWithTransactions: Boolean = false,
    treatTransfersAsIncomeExpense: Boolean = false,

    history: List<Any>,

    upcomingExpanded: Boolean = true,
    setUpcomingExpanded: (Boolean) -> Unit = {},
    upcomingIncome: Double = 0.0,
    upcomingExpenses: Double = 0.0,
    upcoming: List<TransactionOld> = emptyList(),

    overdueExpanded: Boolean = true,
    setOverdueExpanded: (Boolean) -> Unit = {},
    overdueIncome: Double = 0.0,
    overdueExpenses: Double = 0.0,
    overdue: List<TransactionOld> = emptyList(),

    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSetPeriod: (TimePeriod) -> Unit,
    onEditAccount: (AccountOld, Double) -> Unit,
    onEditCategory: (CategoryOld) -> Unit,
    onDelete: () -> Unit,
    onPayOrGet: (TransactionOld) -> Unit = {},
    onSkipTransaction: (TransactionOld) -> Unit = {},
    onSkipAllTransactions: (List<TransactionOld>) -> Unit = {}
) {
    val nav = navigation()
    val itemColor = (account?.color ?: category?.color)?.toComposeColor() ?: Gray

    var deleteModalVisible by remember { mutableStateOf(false) }
    var skipAllModalVisible by remember { mutableStateOf(false) }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
                .clip(UI.shapes.r1Top)
                .background(UI.colors.pure)
                .testTag("item_stats_lazy_column"),
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
                baseData = AppBaseData(
                    baseCurrency, accounts, categories
                ),
                upcoming = DueSection(
                    trns = upcoming,
                    stats = IncomeExpensePair(
                        income = upcomingIncome.toBigDecimal(),
                        expense = upcomingExpenses.toBigDecimal()
                    ),
                    expanded = upcomingExpanded
                ),
                setUpcomingExpanded = setUpcomingExpanded,

                overdue = DueSection(
                    trns = overdue,
                    stats = IncomeExpensePair(
                        income = overdueIncome.toBigDecimal(),
                        expense = overdueExpenses.toBigDecimal()
                    ),
                    expanded = overdueExpanded
                ),
                setOverdueExpanded = setOverdueExpanded,

                history = history,
                lastItemSpacer = 48.dp,

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction,
                onSkipAllTransactions = { skipAllModalVisible = true },
                emptyStateTitle = com.ivy.core.ui.temp.stringRes(R.string.no_transactions),
                emptyStateText = com.ivy.core.ui.temp.stringRes(
                    R.string.no_transactions_for_period,
                    period.toDisplayLong(1)
                )
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

    DeleteModal(
        visible = skipAllModalVisible,
        title = stringResource(R.string.confirm_skip_all),
        description = stringResource(R.string.confirm_skip_all_description),
        dismiss = { skipAllModalVisible = false }
    ) {
        onSkipAllTransactions(overdue)
        skipAllModalVisible = false
    }

    CategoryModal(
        modal = categoryModalData,
        isCategoryParentCategory = isCategoryParentCategory,
        parentCategoryList = parentCategoryList,
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
    history: List<Any>,
    currency: String,
    baseCurrency: String,
    itemColor: Color,
    account: AccountOld?,
    category: CategoryOld?,
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
                            type = TrnTypeOld.INCOME,
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
                            type = TrnTypeOld.EXPENSE,
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
private fun Item(
    itemColor: Color,
    contrastColor: Color,
    account: AccountOld?,
    category: CategoryOld?,

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
                        text = com.ivy.core.ui.temp.stringRes(R.string.excluded),
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
    IvyPreview {
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
            account = AccountOld("DSK", color = GreenDark.toArgb(), icon = "pet"),
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
    IvyPreview {
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
            account = AccountOld(
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

@Preview
@Composable
private fun Preview_empty_upcoming() {
    IvyPreview {
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
            account = AccountOld("DSK", color = GreenDark.toArgb(), icon = "pet"),
            onSetPeriod = { },
            onPreviousMonth = {},
            onNextMonth = {},
            onDelete = {},
            onEditAccount = { _, _ -> },
            onEditCategory = {},
            upcoming = listOf(
                TransactionOld(UUID(1L, 2L), TrnTypeOld.EXPENSE, BigDecimal.valueOf(10L))
            )
        )
    }
}