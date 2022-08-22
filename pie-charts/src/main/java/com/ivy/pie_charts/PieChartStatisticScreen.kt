package com.ivy.pie_charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.screens.EditTransaction
import com.ivy.screens.ItemStatistic
import com.ivy.screens.PieChartStatistic
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Row
import com.ivy.wallet.utils.*

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.PieChartStatisticScreen(
    screen: PieChartStatistic
) {
    val viewModel: PieChartStatisticViewModel = viewModel()
    val state by viewModel.state().collectAsState()

    onScreenStart {
        viewModel.onEvent(PieChartStatisticEvent.Start(screen))
    }

    UI(
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: PieChartStatisticState = PieChartStatisticState(),
    onEventHandler: (PieChartStatisticEvent) -> Unit = {}
) {
    val nav = navigation()
    val lazyState = rememberLazyListState()
    val expanded by remember { derivedStateOf { lazyState.firstVisibleItemIndex < 1 } }
    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounce()
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
                    onEventHandler(PieChartStatisticEvent.OnShowMonthModal(state.period))
                },
                onSelectNextMonth = {
                    onEventHandler(PieChartStatisticEvent.OnSelectNextMonth)
                },
                onSelectPreviousMonth = {
                    onEventHandler(PieChartStatisticEvent.OnSelectPreviousMonth)
                },
                showCloseButtonOnly = state.showCloseButtonOnly,

                onClose = {
                    nav.back()
                },
                onAdd = { trnType ->
                    nav.navigateTo(
                        EditTransaction(
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
                text = if (state.transactionType == TrnType.EXPENSE) stringResource(R.string.expenses) else stringResource(
                    R.string.income
                ),
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

        if (state.showUnpackOption) {
            item {
                IvyCheckboxWithText(
                    modifier = Modifier
                        .padding(top = 12.dp, start = 16.dp),
                    text = stringResource(R.string.unpack_all_subcategories),
                    checked = state.unpackAllSubCategories
                ) {
                    onEventHandler(
                        PieChartStatisticEvent.OnUnpackSubCategories(
                            unpackAllSubCategories = !state.unpackAllSubCategories
                        )
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(40.dp))

            PieChart(
                type = state.transactionType,
                categoryAmounts = state.pieChartCategoryAmount,
                selectedCategory = state.selectedCategory,
                onCategoryClicked = { clickedCategory ->
                    onEventHandler(PieChartStatisticEvent.OnCategoryClicked(clickedCategory))
                }
            )

            Spacer(Modifier.height(48.dp))
        }

        itemsIndexed(
            items = state.categoryAmounts
        ) { index, item ->
            if (item.totalAmount() != 0.0) {
                if (index != 0) {
                    Spacer(Modifier.height(16.dp))
                }
                CategoryAmountCardWithSub(
                    categoryAmount = item,
                    currency = state.baseCurrency,
                    totalAmount = state.totalAmount,
                    selectedCategory = state.selectedCategory,
                    nav = nav,
                    state = state,
                    onEventHandler = onEventHandler
                )
            }
        }

        item {
            Spacer(Modifier.height(160.dp)) //scroll hack
        }
    }

    ChoosePeriodModal(
        modal = state.choosePeriodModal,
        dismiss = {
            onEventHandler(PieChartStatisticEvent.OnShowMonthModal(null))
        }
    ) {
        onEventHandler(PieChartStatisticEvent.OnSetPeriod(it))
    }
}

@Composable
private fun Header(
    transactionType: TrnType,
    period: TimePeriod,
    percentExpanded: Float,

    currency: String,
    amount: Double,
    showCloseButtonOnly: Boolean = false,


    onShowMonthModal: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onSelectPreviousMonth: () -> Unit,

    onClose: () -> Unit,
    onAdd: (TrnType) -> Unit,
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

        //Balance mini row
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
                text = period.toDisplayShort(com.ivy.core.ui.temp.ivyWalletCtx().startDayOfMonth),
            ) {
                onShowMonthModal()
            }

            if (percentExpanded > 0f) {
                Spacer(Modifier.width(12.dp))

                val backgroundGradient = if (transactionType == TrnType.EXPENSE)
                    gradientExpenses() else GradientGreen
                CircleButtonFilledGradient(
                    modifier = Modifier
                        .thenIf(percentExpanded == 1f) {
                            drawColoredShadow(backgroundGradient.startColor)
                        }
                        .alpha(percentExpanded)
                        .size(lerp(1, 40, percentExpanded).dp),
                    iconPadding = 4.dp,
                    icon = R.drawable.ic_plus,
                    backgroundGradient = backgroundGradient,
                    tint = if (transactionType == TrnType.EXPENSE)
                        UI.colors.pure else White
                ) {
                    onAdd(transactionType)
                }
            }

            Spacer(Modifier.width(20.dp))
        }
    }
}

@Composable
private fun CategoryAmountCardWithSub(
    categoryAmount: CategoryAmount,
    currency: String,
    totalAmount: Double,

    selectedCategory: SelectedCategory?,
    nav: Navigation,
    state: PieChartStatisticState,

    onEventHandler: (PieChartStatisticEvent) -> Unit = {},
) {
    CategoryAmountCard(
        categoryAmount = categoryAmount,
        currency = currency,
        totalAmount = totalAmount,
        selectedCategory = selectedCategory,
        onSubCategoryListExpand = {
            onEventHandler(
                PieChartStatisticEvent.OnSubCategoryListExpanded(
                    categoryAmount,
                    !categoryAmount.subCategoryState.subCategoryListExpanded
                )
            )
        }
    ) {
        nav.navigateTo(
            ItemStatistic(
                categoryId = categoryAmount.category?.id,
                unspecifiedCategory = categoryAmount.isCategoryUnspecified,
                accountIdFilterList = state.accountIdFilterList,
                transactions = categoryAmount.associatedTransactions
            )
        )
    }
    if (categoryAmount.subCategoryState.subCategoryListExpanded) {
        Column(modifier = Modifier.padding(start = 24.dp)) {
            categoryAmount.subCategoryState.subCategoriesList.forEach {
                Spacer(Modifier.height(16.dp))
                CategoryAmountCard(
                    categoryAmount = it,
                    currency = currency,
                    totalAmount = totalAmount,
                    selectedCategory = selectedCategory
                ) {
                    nav.navigateTo(
                        ItemStatistic(
                            categoryId = it.category?.id,
                            unspecifiedCategory = it.isCategoryUnspecified,
                            accountIdFilterList = state.accountIdFilterList,
                            transactions = it.associatedTransactions
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryAmountCard(
    categoryAmount: CategoryAmount,
    currency: String,
    totalAmount: Double,

    selectedCategory: SelectedCategory?,

    onSubCategoryListExpand: () -> Unit = {},
    onClick: () -> Unit
) {
    val category = categoryAmount.category
    val amount = categoryAmount.getRelevantAmount()

    val categoryColor = category?.color?.toComposeColor() ?: Gray //Unspecified category = Gray
    val selectedState = when {
        selectedCategory == null -> {
            //no selectedCategory
            false
        }
        categoryAmount.category == selectedCategory.category -> {
            //selectedCategory && we're selected
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    AmountCurrencyB1Row(
                        amount = amount,
                        currency = currency,
                        textColor = textColor,
                        amountFontWeight = FontWeight.ExtraBold
                    )
                }
                if (categoryAmount.subCategoryState.subCategoriesList.isNotEmpty()) {
                    IvyIcon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                onSubCategoryListExpand()
                            },
                        icon = R.drawable.ic_expandarrow,
                        tint = findContrastTextColor(categoryColor)
                    )
                }
            }
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
        text = if (totalAmount != 0.0)
            stringResource(R.string.percent, ((amount / totalAmount) * 100).format(2))
        else stringResource(R.string.percent, "0"),
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
    com.ivy.core.ui.temp.Preview {
        val state = PieChartStatisticState(
            transactionType = TrnType.EXPENSE,
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = listOf(
                CategoryAmount(
                    category = CategoryOld("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                CategoryAmount(
                    category = CategoryOld("Shisha", Orange.toArgb(), icon = "trees"),
                    amount = 411.93
                ),
                CategoryAmount(
                    category = CategoryOld("Food & Drink", IvyDark.toArgb()),
                    amount = 260.03
                ),
                CategoryAmount(
                    category = CategoryOld("Gifts", RedLight.toArgb()),
                    amount = 160.0
                ),
                CategoryAmount(
                    category = CategoryOld("Clothes & Jewelery Fancy", Red.toArgb()),
                    amount = 2.0
                ),
                CategoryAmount(
                    category = CategoryOld(
                        "Finances, Burocracy & Governance",
                        IvyLight.toArgb(),
                        icon = "work"
                    ),
                    amount = 2.0
                ),
            ),
            selectedCategory = null
        )

        UI(state = state)
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Income() {
    com.ivy.core.ui.temp.Preview {
        val state = PieChartStatisticState(
            transactionType = TrnType.INCOME,
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            baseCurrency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = listOf(
                CategoryAmount(
                    category = CategoryOld("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0,
                    isCategoryUnspecified = true
                ),
                CategoryAmount(
                    category = CategoryOld("Shisha", Orange.toArgb(), icon = "trees"),
                    amount = 411.93
                ),
                CategoryAmount(
                    category = CategoryOld("Food & Drink", IvyDark.toArgb()),
                    amount = 260.03
                ),
                CategoryAmount(
                    category = CategoryOld("Gifts", RedLight.toArgb()),
                    amount = 160.0
                ),
                CategoryAmount(
                    category = CategoryOld("Clothes & Jewelery Fancy", Red.toArgb()),
                    amount = 2.0
                ),
                CategoryAmount(
                    category = CategoryOld(
                        "Finances, Burocracy & Governance",
                        IvyLight.toArgb(),
                        icon = "work"
                    ),
                    amount = 2.0
                ),
            ),
            selectedCategory = null
        )

        UI(state = state)
    }
}

