package com.ivy.piechart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.model.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.utils.drawColoredShadow
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.horizontalSwipeListener
import com.ivy.legacy.utils.thenIf
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.TransactionsScreen
import com.ivy.navigation.navigation
import com.ivy.navigation.screenScopedViewModel
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.IvyLight
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.RedLight
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.BalanceRowMini
import com.ivy.wallet.ui.theme.components.CircleButtonFilledGradient
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.components.ItemIconM
import com.ivy.wallet.ui.theme.components.ItemIconMDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.gradientExpenses
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.pureBlur
import com.ivy.wallet.ui.theme.toComposeColor
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Row
import kotlinx.collections.immutable.persistentListOf

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.PieChartStatisticScreen(
    screen: PieChartStatisticScreen
) {
    val viewModel: PieChartStatisticViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(PieChartStatisticEvent.OnStart(screen))
    }

    UI(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: PieChartStatisticState,
    onEvent: (PieChartStatisticEvent) -> Unit = {}
) {
    val nav = navigation()
    val lazyState = rememberLazyListState()
    val expanded = lazyState.firstVisibleItemIndex < 1
    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = com.ivy.legacy.utils.springBounce(),
        label = "percent expanded"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        state = lazyState
    ) {
        stickyHeader {
            Header(
                transactionType = state.transactionType,
                period = state.period,
                percentExpanded = percentExpanded,
                currency = state.baseCurrency,
                amount = state.totalAmount,
                onShowMonthModal = {
                    onEvent(PieChartStatisticEvent.OnShowMonthModal(state.period))
                },
                onSelectNextMonth = {
                    onEvent(PieChartStatisticEvent.OnSelectNextMonth)
                },
                onSelectPreviousMonth = {
                    onEvent(PieChartStatisticEvent.OnSelectPreviousMonth)
                },
                showCloseButtonOnly = state.showCloseButtonOnly,
                onClose = {
                    nav.back()
                },
                onAdd = { trnType ->
                    nav.navigateTo(
                        EditTransactionScreen(
                            initialTransactionId = null,
                            type = trnType
                        )
                    )
                }
            )
        }

        item {
            Spacer(Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .testTag("piechart_title"),
                text = if (state.transactionType == TransactionType.EXPENSE) {
                    stringResource(R.string.expenses)
                } else {
                    stringResource(R.string.income)
                },
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp)
                    .testTag("piechart_total_amount")
                    .alpha(percentExpanded),
                currency = state.baseCurrency,
                balance = state.totalAmount,
                currencyUpfront = false,
                currencyFontSize = 30.sp
            )
        }

        item {
            Spacer(Modifier.height(40.dp))

            PieChart(
                type = state.transactionType,
                categoryAmounts = state.categoryAmounts,
                selectedCategory = state.selectedCategory,
                onCategoryClicked = { clickedCategory ->
                    onEvent(PieChartStatisticEvent.OnCategoryClicked(clickedCategory))
                }
            )

            Spacer(Modifier.height(48.dp))
        }

        itemsIndexed(
            items = state.categoryAmounts
        ) { index, item ->
            if (item.amount != 0.0) {
                if (index != 0) {
                    Spacer(Modifier.height(16.dp))
                }

                CategoryAmountCard(
                    categoryAmount = item,
                    currency = state.baseCurrency,
                    totalAmount = state.totalAmount,
                    selectedCategory = state.selectedCategory
                ) {
                    nav.navigateTo(
                        TransactionsScreen(
                            categoryId = item.category?.id,
                            unspecifiedCategory = item.isCategoryUnspecified,
                            accountIdFilterList = state.accountIdFilterList,
                            transactions = item.associatedTransactions
                        )
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(160.dp)) // scroll hack
        }
    }

    ChoosePeriodModal(
        modal = state.choosePeriodModal,
        dismiss = {
            onEvent(PieChartStatisticEvent.OnShowMonthModal(null))
        }
    ) {
        onEvent(PieChartStatisticEvent.OnSetPeriod(it))
    }
}

@Composable
private fun Header(
    transactionType: TransactionType,
    period: com.ivy.legacy.data.model.TimePeriod,
    percentExpanded: Float,

    currency: String,
    amount: Double,

    onShowMonthModal: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onSelectPreviousMonth: () -> Unit,

    onClose: () -> Unit,
    onAdd: (TransactionType) -> Unit,
    showCloseButtonOnly: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(pureBlur())
            .statusBarsPadding()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        CloseButton {
            onClose()
        }

        // Balance mini row
        if (percentExpanded < 1f) {
            Spacer(Modifier.width(12.dp))

            BalanceRowMini(
                modifier = Modifier
                    .alpha(1f - percentExpanded),
                currency = currency,
                balance = amount,
            )
        }

        if (!showCloseButtonOnly) {
            Spacer(Modifier.weight(1f))

            IvyOutlinedButton(
                modifier = Modifier.horizontalSwipeListener(
                    sensitivity = 75,
                    onSwipeLeft = {
                        onSelectNextMonth()
                    },
                    onSwipeRight = {
                        onSelectPreviousMonth()
                    }
                ),
                iconStart = R.drawable.ic_calendar,
                text = period.toDisplayShort(com.ivy.legacy.ivyWalletCtx().startDayOfMonth),
            ) {
                onShowMonthModal()
            }

            if (percentExpanded > 0f) {
                Spacer(Modifier.width(12.dp))

                val backgroundGradient = if (transactionType == TransactionType.EXPENSE) {
                    gradientExpenses()
                } else {
                    GradientGreen
                }
                CircleButtonFilledGradient(
                    modifier = Modifier
                        .thenIf(percentExpanded == 1f) {
                            drawColoredShadow(backgroundGradient.startColor)
                        }
                        .alpha(percentExpanded)
                        .size(com.ivy.legacy.utils.lerp(1, 40, percentExpanded).dp),
                    iconPadding = 4.dp,
                    icon = R.drawable.ic_plus,
                    backgroundGradient = backgroundGradient,
                    tint = if (transactionType == TransactionType.EXPENSE) {
                        UI.colors.pure
                    } else {
                        White
                    }
                ) {
                    onAdd(transactionType)
                }
            }

            Spacer(Modifier.width(20.dp))
        }
    }
}

@Composable
private fun CategoryAmountCard(
    categoryAmount: CategoryAmount,
    currency: String,
    totalAmount: Double,

    selectedCategory: SelectedCategory?,

    onClick: () -> Unit
) {
    val category = categoryAmount.category
    val amount = categoryAmount.amount

    val categoryColor = category?.color?.toComposeColor() ?: Gray // Unspecified category = Gray
    val selectedState = when {
        selectedCategory == null -> {
            // no selectedCategory
            false
        }

        categoryAmount.category == selectedCategory.category -> {
            // selectedCategory && we're selected
            true
        }

        else -> false
    }
    val backgroundColor = if (selectedState) categoryColor else UI.colors.medium

    val textColor = findContrastTextColor(
        backgroundColor = backgroundColor
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .thenIf(selectedState) {
                drawColoredShadow(backgroundColor)
            }
            .clip(UI.shapes.r3)
            .background(backgroundColor, UI.shapes.r3)
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        ItemIconM(
            modifier = Modifier.background(categoryColor, CircleShape),
            iconName = category?.icon,
            tint = findContrastTextColor(categoryColor),
            iconContentScale = ContentScale.None,
            Default = {
                ItemIconMDefaultIcon(
                    modifier = Modifier.background(categoryColor, CircleShape),
                    iconName = category?.icon,
                    defaultIcon = R.drawable.ic_custom_category_m,
                    tint = findContrastTextColor(categoryColor)
                )
            }
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    text = category?.name ?: stringResource(R.string.unspecified),
                    style = UI.typo.b2.style(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )

                PercentText(
                    amount = amount,
                    totalAmount = totalAmount,
                    selectedState = selectedState,
                    contrastColor = textColor
                )

                Spacer(Modifier.width(24.dp))
            }

            Spacer(Modifier.height(4.dp))

            AmountCurrencyB1Row(
                amount = amount,
                currency = currency,
                textColor = textColor,
                amountFontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun PercentText(
    amount: Double,
    totalAmount: Double,
    selectedState: Boolean,
    contrastColor: Color
) {
    Text(
        text = if (totalAmount != 0.0) {
            stringResource(R.string.percent, ((amount / totalAmount) * 100).format(2))
        } else {
            stringResource(R.string.percent, "0")
        },
        style = UI.typo.nB2.style(
            color = if (selectedState) contrastColor else UI.colors.pureInverse,
            fontWeight = FontWeight.Normal
        )
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Expense() {
    com.ivy.legacy.IvyWalletPreview {
        val state = PieChartStatisticState(
            transactionType = TransactionType.EXPENSE,
            period = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = persistentListOf(
                CategoryAmount(
                    category = Category("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                CategoryAmount(
                    category = Category("Shisha", Orange.toArgb(), icon = "trees"),
                    amount = 411.93
                ),
                CategoryAmount(
                    category = Category("Food & Drink", IvyDark.toArgb()),
                    amount = 260.03
                ),
                CategoryAmount(
                    category = Category("Gifts", RedLight.toArgb()),
                    amount = 160.0
                ),
                CategoryAmount(
                    category = Category("Clothes & Jewelery Fancy", Red.toArgb()),
                    amount = 2.0
                ),
                CategoryAmount(
                    category = Category(
                        "Finances, Burocracy & Governance",
                        IvyLight.toArgb(),
                        icon = "work"
                    ),
                    amount = 2.0
                ),
            ),
            selectedCategory = null,
            accountIdFilterList = persistentListOf(),
            choosePeriodModal = null,
            filterExcluded = false,
            showCloseButtonOnly = false,
            transactions = persistentListOf()
        )

        UI(state = state)
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Income() {
    com.ivy.legacy.IvyWalletPreview {
        val state = PieChartStatisticState(
            transactionType = TransactionType.INCOME,
            period = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = persistentListOf(
                CategoryAmount(
                    category = Category("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                CategoryAmount(
                    category = Category("Shisha", Orange.toArgb(), icon = "trees"),
                    amount = 411.93
                ),
                CategoryAmount(
                    category = Category("Food & Drink", IvyDark.toArgb()),
                    amount = 260.03
                ),
                CategoryAmount(
                    category = Category("Gifts", RedLight.toArgb()),
                    amount = 160.0
                ),
                CategoryAmount(
                    category = Category("Clothes & Jewelery Fancy", Red.toArgb()),
                    amount = 2.0
                ),
                CategoryAmount(
                    category = Category(
                        "Finances, Burocracy & Governance",
                        IvyLight.toArgb(),
                        icon = "work"
                    ),
                    amount = 2.0
                ),
            ),
            selectedCategory = null,
            accountIdFilterList = persistentListOf(),
            choosePeriodModal = null,
            filterExcluded = false,
            showCloseButtonOnly = false,
            transactions = persistentListOf()
        )

        UI(state = state)
    }
}