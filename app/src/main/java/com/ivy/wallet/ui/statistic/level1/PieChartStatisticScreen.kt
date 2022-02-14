package com.ivy.wallet.ui.statistic.level1

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.api.navigation
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.*
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Row

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.PieChartStatisticScreen(
    screen: PieChartStatistic
) {
    val viewModel: PieChartStatisticViewModel = viewModel()

    val ivyContext = ivyWalletCtx()

    val type by viewModel.type.collectAsState()
    val period by viewModel.period.collectAsState()
    val currency by viewModel.baseCurrencyCode.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val categoryAmounts by viewModel.categoryAmounts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    onScreenStart {
        viewModel.start(screen)
    }

    UI(
        transactionType = type,
        period = period,
        currency = currency,
        totalAmount = totalAmount,
        categoryAmounts = categoryAmounts,
        selectedCategory = selectedCategory,

        onSetPeriod = viewModel::onSetPeriod,
        onSelectNextMonth = viewModel::nextMonth,
        onSelectPreviousMonth = viewModel::previousMonth,
        onSetSelectedCategory = viewModel::setSelectedCategory
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    transactionType: TransactionType,
    period: TimePeriod,
    currency: String,
    totalAmount: Double,
    categoryAmounts: List<CategoryAmount>,
    selectedCategory: SelectedCategory?,

    onSelectNextMonth: () -> Unit = {},
    onSelectPreviousMonth: () -> Unit = {},
    onSetPeriod: (TimePeriod) -> Unit = {},
    onSetSelectedCategory: (SelectedCategory?) -> Unit = {}
) {
    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }

    val lazyState = rememberLazyListState()

    val expanded = lazyState.firstVisibleItemIndex < 1
    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounce()
    )
    val nav = navigation()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        state = lazyState
    ) {
        stickyHeader {
            Header(
                transactionType = transactionType,
                period = period,
                percentExpanded = percentExpanded,

                currency = currency,
                amount = totalAmount,

                onShowMonthModal = {
                    choosePeriodModal = ChoosePeriodModalData(
                        period = period
                    )
                },
                onSelectNextMonth = onSelectNextMonth,
                onSelectPreviousMonth = onSelectPreviousMonth,

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
                modifier = Modifier.padding(start = 32.dp),
                text = if (transactionType == TransactionType.EXPENSE) "Expenses" else "Income",
                style = Typo.body1.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp)
                    .alpha(percentExpanded),
                currency = currency,
                balance = totalAmount,
                currencyUpfront = false,
                currencyFontSize = 30.sp
            )
        }

        item {
            Spacer(Modifier.height(40.dp))

            PieChart(
                type = transactionType,
                categoryAmounts = categoryAmounts,
                selectedCategory = selectedCategory,
                onCategoryClicked = { clickedCategory ->
                    //null - Unspecified
                    if (selectedCategory == null) {
                        //no category selected, select the click one
                        selectCategory(
                            categoryToSelect = clickedCategory,
                            setSelectedCategory = onSetSelectedCategory
                        )
                    } else {
                        //category selected
                        val currentlySelected = selectedCategory.category
                        if (currentlySelected == clickedCategory) {
                            //deselect clicked category
                            onSetSelectedCategory(null)
                        } else {
                            //select new category
                            selectCategory(
                                categoryToSelect = clickedCategory,
                                setSelectedCategory = onSetSelectedCategory
                            )
                        }
                    }
                }
            )

            Spacer(Modifier.height(48.dp))
        }

        itemsIndexed(
            items = categoryAmounts
        ) { index, item ->
            if (item.amount != 0.0) {
                if (index != 0) {
                    Spacer(Modifier.height(16.dp))
                }

                CategoryAmountCard(
                    categoryAmount = item,
                    currency = currency,
                    totalAmount = totalAmount,

                    selectedCategory = selectedCategory
                ) {
                    nav.navigateTo(
                        ItemStatistic(
                            categoryId = item.category?.id,
                            unspecifiedCategory = item.category == null
                        )
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(160.dp)) //scroll hack
        }
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        }
    ) {
        onSetPeriod(it)
    }
}

private fun selectCategory(
    categoryToSelect: Category?,

    setSelectedCategory: (SelectedCategory?) -> Unit
) {
    setSelectedCategory(
        SelectedCategory(
            category = categoryToSelect
        )
    )
}

@Composable
private fun Header(
    transactionType: TransactionType,
    period: TimePeriod,
    percentExpanded: Float,

    currency: String,
    amount: Double,


    onShowMonthModal: () -> Unit,
    onSelectNextMonth: () -> Unit,
    onSelectPreviousMonth: () -> Unit,

    onClose: () -> Unit,
    onAdd: (TransactionType) -> Unit,
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
            text = period.toDisplayShort(ivyWalletCtx().startDayOfMonth),
        ) {
            onShowMonthModal()
        }

        if (percentExpanded > 0f) {
            Spacer(Modifier.width(12.dp))

            val backgroundGradient = if (transactionType == TransactionType.EXPENSE)
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
                tint = if (transactionType == TransactionType.EXPENSE)
                    IvyTheme.colors.pure else White
            ) {
                onAdd(transactionType)
            }
        }

        Spacer(Modifier.width(20.dp))
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
    val backgroundColor = if (selectedState) categoryColor else IvyTheme.colors.medium

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
            .clip(Shapes.rounded20)
            .background(backgroundColor, Shapes.rounded20)
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        ItemIconMDefaultIcon(
            modifier = Modifier.background(categoryColor, CircleShape),
            iconName = category?.icon,
            defaultIcon = R.drawable.ic_custom_category_m,
            tint = findContrastTextColor(categoryColor)
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
                    text = category?.name ?: "Unspecified",
                    style = Typo.body2.style(
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
        text = if (totalAmount != 0.0) "${((amount / totalAmount) * 100).format(2)}%" else "0%",
        style = Typo.numberBody2.style(
            color = if (selectedState) contrastColor else IvyTheme.colors.pureInverse,
            fontWeight = FontWeight.Normal
        )
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Expense() {
    IvyAppPreview {
        UI(
            transactionType = TransactionType.EXPENSE,
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            currency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = listOf(
                CategoryAmount(
                    category = Category("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0
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
            selectedCategory = null
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_Income() {
    IvyAppPreview {
        UI(
            transactionType = TransactionType.INCOME,
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            currency = "BGN",
            totalAmount = 1828.0,
            categoryAmounts = listOf(
                CategoryAmount(
                    category = Category("Bills", Green.toArgb(), icon = "bills"),
                    amount = 791.0
                ),
                CategoryAmount(
                    category = null,
                    amount = 497.0
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
            selectedCategory = null
        )
    }
}

