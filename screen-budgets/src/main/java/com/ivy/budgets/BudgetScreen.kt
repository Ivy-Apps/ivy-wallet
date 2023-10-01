package com.ivy.budgets

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.budgets.model.DisplayBudget
import com.ivy.legacy.datamodel.Budget
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation
import com.ivy.legacy.legacy.ui.theme.components.BudgetBattery
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.legacy.utils.format
import com.ivy.navigation.BudgetScreen
import com.ivy.navigation.screenScopedViewModel
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.ReorderButton
import com.ivy.wallet.ui.theme.components.ReorderModalSingleType
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BoxWithConstraintsScope.BudgetScreen(screen: BudgetScreen) {
    val viewModel: BudgetViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    UI(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: BudgetScreenState,
    onEvent: (BudgetScreenEvent) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            timeRange = state.timeRange,
            baseCurrency = state.baseCurrency,
            appBudgetMax = state.appBudgetMax,
            categoryBudgetsTotal = state.categoryBudgetsTotal,
            setReorderModalVisible = {
                onEvent(BudgetScreenEvent.OnReorderModalVisible(it))
            }
        )

        Spacer(Modifier.height(8.dp))

        for (item in state.budgets) {
            Spacer(Modifier.height(24.dp))

            BudgetItem(
                displayBudget = item,
                baseCurrency = state.baseCurrency
            ) {
                onEvent(
                    BudgetScreenEvent.OnBudgetModalData(
                        BudgetModalData(
                            budget = item.budget,
                            baseCurrency = state.baseCurrency,
                            categories = state.categories,
                            accounts = state.accounts,
                            autoFocusKeyboard = false
                        )
                    )
                )
            }
        }

        if (state.budgets.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoBudgetsEmptyState(
                emptyStateTitle = stringResource(R.string.no_budgets),
                emptyStateText = stringResource(R.string.no_budgets_text)
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp)) // scroll hack
    }

    val nav = navigation()
    BudgetBottomBar(
        onAdd = {
            onEvent(
                BudgetScreenEvent.OnBudgetModalData(
                    BudgetModalData(
                        budget = null,
                        baseCurrency = state.baseCurrency,
                        categories = state.categories,
                        accounts = state.accounts
                    )
                )
            )
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.budgets,
        dismiss = {
            onEvent(BudgetScreenEvent.OnReorderModalVisible(false))
        },
        onReordered = { onEvent(BudgetScreenEvent.OnReorder(it)) }
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
        modal = state.budgetModalData,
        onCreate = { onEvent(BudgetScreenEvent.OnCreateBudget(it)) },
        onEdit = { onEvent(BudgetScreenEvent.OnEditBudget(it)) },
        onDelete = { onEvent(BudgetScreenEvent.OnDeleteBudget(it)) },
        dismiss = {
            onEvent(BudgetScreenEvent.OnBudgetModalData(null))
        }
    )
}

@Composable
private fun Toolbar(
    timeRange: com.ivy.legacy.data.model.FromToTimeRange?,
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
                } else {
                    ""
                }

                val appBudgetMaxText = if (appBudgetMax > 0) {
                    stringResource(
                        R.string.app_budget,
                        appBudgetMax.format(baseCurrency),
                        baseCurrency
                    )
                } else {
                    ""
                }

                val hasBothBudgetTypes =
                    categoryBudgetText.isNotBlank() && appBudgetMaxText.isNotBlank()
                Text(
                    modifier = Modifier.testTag("budgets_info_text"),
                    text = if (hasBothBudgetTypes) {
                        stringResource(
                            R.string.budget_info_both,
                            categoryBudgetText,
                            appBudgetMaxText
                        )
                    } else {
                        stringResource(R.string.budget_info, categoryBudgetText, appBudgetMaxText)
                    },
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
                text = determineBudgetType(displayBudget.budget.parseCategoryIds().size),
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
    emptyStateTitle: String,
    emptyStateText: String,
    modifier: Modifier = Modifier
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
    com.ivy.legacy.IvyWalletPreview {
        UI(
            state = BudgetScreenState(
                timeRange = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                    startDayOfMonth = 1
                ).toRange(1), // preview
                baseCurrency = "BGN",
                categories = persistentListOf(),
                accounts = persistentListOf(),
                budgets = persistentListOf(),
                appBudgetMax = 5000.0,
                categoryBudgetsTotal = 2400.0,
                budgetModalData = null,
                reorderModalVisible = false
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Budgets() {
    com.ivy.legacy.IvyWalletPreview {
        UI(
            state = BudgetScreenState(
                timeRange = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                    startDayOfMonth = 1
                ).toRange(1), // preview
                baseCurrency = "BGN",
                categories = persistentListOf(),
                accounts = persistentListOf(),
                appBudgetMax = 5000.0,
                categoryBudgetsTotal = 0.0,
                budgetModalData = null,
                reorderModalVisible = false,
                budgets = persistentListOf(
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
                    )
                )
            )
        )
    }
}
