package com.ivy.wallet.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Budget
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.ui.BudgetScreen
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.budget.model.DisplayBudget
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.BudgetBattery
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.ReorderButton
import com.ivy.wallet.ui.theme.components.ReorderModalSingleType
import com.ivy.wallet.ui.theme.modal.BudgetModal
import com.ivy.wallet.ui.theme.modal.BudgetModalData
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import com.ivy.wallet.utils.clickableNoIndication
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.onScreenStart

@Composable
fun BoxWithConstraintsScope.BudgetScreen(screen: BudgetScreen) {
    val viewModel: BudgetViewModel = viewModel()

    val timeRange by viewModel.timeRange.collectAsState()
    val baseCurrency by viewModel.baseCurrencyCode.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val appBudgetMax by viewModel.appBudgetMax.collectAsState()
    val categoryBudgetsTotal by viewModel.categoryBudgetsTotal.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        timeRange = timeRange,
        baseCurrency = baseCurrency,
        categories = categories,
        accounts = accounts,
        displayBudgets = budgets,
        appBudgetMax = appBudgetMax,
        categoryBudgetsTotal = categoryBudgetsTotal,

        onCreateBudget = viewModel::createBudget,
        onEditBudget = viewModel::editBudget,
        onDeleteBudget = viewModel::deleteBudget,
        onReorder = viewModel::reorder
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    timeRange: FromToTimeRange?,
    baseCurrency: String,
    categories: List<Category>,
    accounts: List<Account>,
    displayBudgets: List<DisplayBudget>,
    appBudgetMax: Double,
    categoryBudgetsTotal: Double,

    onCreateBudget: (CreateBudgetData) -> Unit = {},
    onEditBudget: (Budget) -> Unit = {},
    onDeleteBudget: (Budget) -> Unit = {},
    onReorder: (List<DisplayBudget>) -> Unit = {}
) {
    var reorderModalVisible by remember { mutableStateOf(false) }
    var budgetModalData: BudgetModalData? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            timeRange = timeRange,
            baseCurrency = baseCurrency,
            appBudgetMax = appBudgetMax,
            categoryBudgetsTotal = categoryBudgetsTotal,
            setReorderModalVisible = {
                reorderModalVisible = it
            }
        )

        Spacer(Modifier.height(8.dp))

        for (item in displayBudgets) {
            Spacer(Modifier.height(24.dp))

            BudgetItem(
                displayBudget = item,
                baseCurrency = baseCurrency
            ) {
                budgetModalData = BudgetModalData(
                    budget = item.budget,
                    baseCurrency = baseCurrency,
                    categories = categories,
                    accounts = accounts,
                    autoFocusKeyboard = false
                )
            }
        }

        if (displayBudgets.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoBudgetsEmptyState(
                emptyStateTitle = stringResource(R.string.no_budgets),
                emptyStateText = stringResource(R.string.no_budgets_text)
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp))  //scroll hack
    }

    val nav = navigation()
    BudgetBottomBar(
        onAdd = {
            budgetModalData = BudgetModalData(
                budget = null,
                baseCurrency = baseCurrency,
                categories = categories,
                accounts = accounts
            )
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = reorderModalVisible,
        initialItems = displayBudgets,
        dismiss = {
            reorderModalVisible = false
        },
        onReordered = onReorder
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.budget.name,
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )
    }

    BudgetModal(
        modal = budgetModalData,
        onCreate = onCreateBudget,
        onEdit = onEditBudget,
        onDelete = onDeleteBudget,
        dismiss = {
            budgetModalData = null
        }
    )
}

@Composable
private fun Toolbar(
    timeRange: FromToTimeRange?,
    baseCurrency: String,
    appBudgetMax: Double,
    categoryBudgetsTotal: Double,

    setReorderModalVisible: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.budgets),
                style = UI.typo.h2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (timeRange != null) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = timeRange.toDisplay(),
                    style = UI.typo.b2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (categoryBudgetsTotal > 0 || appBudgetMax > 0) {
                Spacer(Modifier.height(4.dp))

                val categoryBudgetText = if (categoryBudgetsTotal > 0) {
                    stringResource(
                        R.string.for_categories,
                        categoryBudgetsTotal.format(baseCurrency),
                        baseCurrency
                    )
                } else ""

                val appBudgetMaxText = if (appBudgetMax > 0) {
                    stringResource(
                        R.string.app_budget,
                        appBudgetMax.format(baseCurrency),
                        baseCurrency
                    )
                } else ""

                val hasBothBudgetTypes =
                    categoryBudgetText.isNotBlank() && appBudgetMaxText.isNotBlank()
                Text(
                    modifier = Modifier.testTag("budgets_info_text"),
                    text = if (hasBothBudgetTypes)
                        stringResource(
                            R.string.budget_info_both,
                            categoryBudgetText,
                            appBudgetMaxText
                        )
                    else stringResource(R.string.budget_info, categoryBudgetText, appBudgetMaxText),
                    style = UI.typo.nC.style(
                        color = Gray,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

        }

        ReorderButton {
            setReorderModalVisible(true)
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun BudgetItem(
    displayBudget: DisplayBudget,
    baseCurrency: String,

    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoIndication {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = displayBudget.budget.name,
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = Budget.type(displayBudget.budget.parseCategoryIds().size),
                style = UI.typo.c.style(
                    color = Gray
                )
            )
        }


        AmountCurrencyB1(
            amount = displayBudget.budget.amount,
            currency = baseCurrency,
            amountFontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.width(32.dp))
    }

    Spacer(Modifier.height(12.dp))

    BudgetBattery(
        modifier = Modifier.padding(horizontal = 16.dp),
        currency = baseCurrency,
        expenses = displayBudget.spentAmount,
        budget = displayBudget.budget.amount,
        backgroundNotFilled = UI.colors.medium
    ) {
        onClick()
    }
}

@Composable
private fun NoBudgetsEmptyState(
    modifier: Modifier = Modifier,
    emptyStateTitle: String,
    emptyStateText: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        IvyIcon(
            icon = R.drawable.ic_budget_xl,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = emptyStateTitle,
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}


@Preview
@Composable
private fun Preview_Empty() {
    IvyWalletPreview {
        UI(
            timeRange = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ).toRange(1), //preview
            baseCurrency = "BGN",
            categories = emptyList(),
            accounts = emptyList(),
            displayBudgets = emptyList(),
            appBudgetMax = 5000.0,
            categoryBudgetsTotal = 2400.0,

            onReorder = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Budgets() {
    IvyWalletPreview {
        UI(
            timeRange = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ).toRange(1), //preview
            baseCurrency = "BGN",
            categories = emptyList(),
            accounts = emptyList(),
            appBudgetMax = 5000.0,
            categoryBudgetsTotal = 0.0,
            displayBudgets = listOf(
                DisplayBudget(
                    budget = Budget(
                        name = "Ivy Marketing",
                        amount = 1000.0,
                        accountIdsSerialized = null,
                        categoryIdsSerialized = null,
                        orderId = 0.0
                    ),
                    spentAmount = 260.0
                ),

                DisplayBudget(
                    budget = Budget(
                        name = "Ivy Marketing 2",
                        amount = 1000.0,
                        accountIdsSerialized = null,
                        categoryIdsSerialized = null,
                        orderId = 0.0
                    ),
                    spentAmount = 351.0
                ),

                DisplayBudget(
                    budget = Budget(
                        name = "Baldr Products, Fidgets",
                        amount = 750.0,
                        accountIdsSerialized = null,
                        categoryIdsSerialized = "cat1,cat2,cat3",
                        orderId = 0.1
                    ),
                    spentAmount = 50.0
                ),
            ),

            onReorder = {}
        )
    }
}
