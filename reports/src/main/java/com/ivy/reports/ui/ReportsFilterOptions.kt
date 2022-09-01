package com.ivy.reports.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.base.R
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.ui.color.contrast
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.temp.ivyWalletCtx
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.core.ui.transaction.defaultExpandCollapseHandler
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.icon.IconSize
import com.ivy.data.icon.IvyIcon
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.reports.*
import com.ivy.reports.states.FilterState
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.AddKeywordModal
import com.ivy.wallet.ui.theme.modal.AddModalBackHandling
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Row
import com.ivy.wallet.utils.capitalizeLocal
import com.ivy.wallet.utils.thenIf
import java.util.*

@Composable
fun BoxWithConstraintsScope.ReportsFilterOptions(
    baseCurrency: String,
    state: FilterState,
    onClose: () -> Unit,
    onFilterEvent: (ReportFilterEvent) -> Unit
) {
    val ivyContext = ivyWalletCtx()
    val modalId = remember { UUID.randomUUID() }

    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }
    val onPeriodClicked = remember {
        {
            choosePeriodModal = ChoosePeriodModalData(
                period = state.period.data ?: ivyContext.selectedPeriod
            )
        }
    }

    val amtFilterHandler = defaultExpandCollapseHandler()
    var amtFilterType: AmountFilterType by remember {
        mutableStateOf(AmountFilterType.MIN)  //Default value
    }
    val onAmountClick: (AmountFilterType) -> Unit = remember {
        {
            amtFilterType = it
            amtFilterHandler.expand()
        }
    }

    val keywordFilterHandler = defaultExpandCollapseHandler()
    var keywordsFilterType: KeywordsFilterType by remember {
        mutableStateOf(KeywordsFilterType.INCLUDE)
    }

    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(easing = LinearOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(UI.colors.pure)
                .systemBarsPadding()
        )
        {
            LazyColumn {
                item {
                    FilterHeader(
                        modifier = Modifier.padding(vertical = 24.dp),
                        onClearFilter = {
                            onFilterEvent(ReportFilterEvent.Clear(ClearType.ALL))
                        }
                    )
                }

                item {
                    TypeFilter(transactionTypes = state.selectedTrnTypes) { checked, trnType ->
                        onFilterEvent(
                            ReportFilterEvent.SelectTrnsType(
                                type = trnType,
                                checked = checked
                            )
                        )
                    }

                    FilterDivider()
                }

                item {
                    PeriodFilter(
                        period = state.period,
                        onShowPeriodChooserModal = onPeriodClicked
                    )

                    FilterDivider()
                }

                item {
                    AccountsFilter(
                        allAccounts = state.allAccounts,
                        selectedAccounts = state.selectedAcc,
                        onClearAll = { onFilterEvent(ReportFilterEvent.Clear(ClearType.ACCOUNTS)) },
                        onSelectAll = { onFilterEvent(ReportFilterEvent.SelectAll(SelectType.ACCOUNTS)) }
                    ) { selected, account ->
                        onFilterEvent(
                            ReportFilterEvent.SelectAccount(
                                account = account,
                                add = selected
                            )
                        )
                    }

                    FilterDivider()
                }

                item {
                    CategoriesFilter(
                        allCategories = state.allCategories,
                        selectedCategories = state.selectedCat,
                        onClearAll = { onFilterEvent(ReportFilterEvent.Clear(ClearType.CATEGORIES)) },
                        onSelectAll = { onFilterEvent(ReportFilterEvent.SelectAll(SelectType.CATEGORIES)) }
                    ) { selected, category ->
                        onFilterEvent(
                            ReportFilterEvent.SelectCategory(
                                category = category,
                                add = selected
                            )
                        )
                    }

                    FilterDivider()
                }

                item {
                    AmountFilter(
                        baseCurrency = baseCurrency,
                        minAmount = state.minAmount,
                        maxAmount = state.maxAmount,
                        onClick = onAmountClick
                    )

                    FilterDivider()
                }

                item {
                    KeywordsFilter(
                        includedKeywords = state.includeKeywords,
                        excludedKeywords = state.excludeKeywords,
                        onAdd = {
                            keywordsFilterType = it
                            keywordFilterHandler.expand()
                        }
                    ) { actionType, item ->
                        onFilterEvent(
                            ReportFilterEvent.SelectKeyword(
                                keywordsFilterType = actionType,
                                keyword = item,
                                add = false
                            )
                        )
                    }

                    FilterFooter()
                }
            }

            FilterOptions(
                onClose = onClose,
                onApplyFilter = {
                    onFilterEvent(ReportFilterEvent.FilterSet(state))
                    onClose()
                }
            )
        }
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = { choosePeriodModal = null },
    ) { selectedPeriod ->
        onFilterEvent(ReportFilterEvent.SelectPeriod(selectedPeriod))
    }

    AmountModal(
        id = UUID.randomUUID(),
        visible = amtFilterHandler.expanded,
        currency = baseCurrency,
        initialAmount = when (amtFilterType) {
            AmountFilterType.MIN -> state.minAmount
            AmountFilterType.MAX -> state.maxAmount
        },
        dismiss = {
            amtFilterHandler.collapse()
        },
        onAmountChanged = {
            onFilterEvent(ReportFilterEvent.SelectAmount(amtFilterType, it))
        }
    )

    AddKeywordModal(
        keyword = "",
        visible = keywordFilterHandler.expanded,
        dismiss = { keywordFilterHandler.collapse() }
    ) { keyword ->
        onFilterEvent(ReportFilterEvent.SelectKeyword(keywordsFilterType, keyword, add = true))

    }

    AddBackHandlingSupport(
        id = modalId,
        visible = state.visible,
        action = onClose
    )
}

@Composable
fun FilterFooter() {
    SpacerVer(height = 196.dp)
}

@Composable
fun BoxScope.FilterOptions(onClose: () -> Unit, onApplyFilter: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .zIndex(200f)
            .background(Gradient(Transparent, UI.colors.pure).asVerticalBrush())
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(Modifier.width(24.dp))

        CloseButton(onClick = onClose)

        Spacer(Modifier.weight(1f))

        IvyButton(
            text = stringResource(R.string.apply_filter),
            iconStart = R.drawable.ic_filter_xs,
            backgroundGradient = GradientGreen,
            padding = 10.dp,
        ) {
            onApplyFilter()
        }

        Spacer(Modifier.width(24.dp))
    }

}


@Composable
private fun AddBackHandlingSupport(id: UUID, visible: Boolean, action: () -> Unit) {
    AddModalBackHandling(
        modalId = id,
        visible = visible,
        action = action
    )
}

@Composable
private fun FilterHeader(modifier: Modifier = Modifier, onClearFilter: () -> Unit) {
    LogCompositions(tag = TAG, msg = "Filter Header")
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.filter),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable {
                    onClearFilter()
                }
                .padding(all = 4.dp), //expand click area
            text = stringResource(R.string.clean_filter),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun TypeFilter(
    transactionTypes: ImmutableData<List<TrnType>>,
    onChecked: (Boolean, TrnType) -> Unit
) {
    LogCompositions(tag = TAG, msg = "Transaction Types")
    FilterTitleText(
        text = stringResource(R.string.by_type),
        active = transactionTypes.data.isNotEmpty(),
        inactiveColor = Red
    )

    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyCheckboxWithText(
            text = stringResource(id = R.string.income),
            checked = transactionTypes.data.contains(TrnType.INCOME)
        ) {
            onChecked(it, TrnType.INCOME)
        }

        Spacer(Modifier.width(20.dp))

        IvyCheckboxWithText(
            text = stringResource(id = R.string.expense),
            checked = transactionTypes.data.contains(TrnType.EXPENSE)
        ) {
            onChecked(it, TrnType.EXPENSE)
        }
    }

    Spacer(Modifier.height(4.dp))

    IvyCheckboxWithText(
        modifier = Modifier.padding(start = 20.dp),
        text = stringResource(id = R.string.transfer),
        checked = transactionTypes.data.contains(TrnType.TRANSFER)
    ) {
        onChecked(it, TrnType.TRANSFER)
    }
}

@Composable
private fun FilterTitleText(
    text: String,
    active: Boolean,
    inactiveColor: Color = Color.Gray
) {
    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = text,
        style = UI.typo.b1.style(
            fontWeight = FontWeight.Medium,
            color = if (active) UI.colors.pureInverse else inactiveColor
        )
    )
}

@Composable
private fun PeriodFilter(
    period: ImmutableData<TimePeriod?>,
    onShowPeriodChooserModal: () -> Unit
) {
    LogCompositions(tag = TAG, msg = "TimePeriod")
    val ctx = ivyWalletCtx()
    val defaultText = stringResource(R.string.select_time_range)
    val text by remember(period) {
        mutableStateOf(
            period.data?.toDisplayLong(ctx.startDayOfMonth)
                ?.capitalizeLocal()
                ?: defaultText
        )
    }

    FilterTitleText(
        text = stringResource(R.string.time_period),
        active = period.data != null,
        inactiveColor = Red
    )

    Spacer(Modifier.height(16.dp))

    IvyOutlinedButtonFillMaxWidth(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        iconStart = R.drawable.ic_calendar,
        text = text,
        padding = 12.dp,
    ) {
        onShowPeriodChooserModal()
    }
}

@Composable
private fun AccountsFilter(
    allAccounts: ImmutableData<List<Account>>,
    selectedAccounts: ImmutableData<List<Account>>,
    onClearAll: () -> Unit,
    onSelectAll: () -> Unit,
    onItemClick: (Boolean, Account) -> Unit
) {
    LogCompositions(tag = TAG, msg = "Accounts Filter")
    ListFilterTitle(
        text = stringResource(R.string.accounts_number, selectedAccounts.data.size),
        active = selectedAccounts.data.isNotEmpty(),
        itemsSelected = selectedAccounts.data.size,
        onClearAll = onClearAll,
        onSelectAll = onSelectAll
    )

    Spacer(Modifier.height(16.dp))

    LazyRow(modifier = Modifier.padding(start = 24.dp)) {
        items(
            items = allAccounts.data,
            key = {
                it.id.toString()
            }
        ) { account ->
            //LogCompositions(tag = TAG, msg = "Accounts Filter LazyColumn")
            SelectionBadges(
                text = account.name,
                icon = account.icon,
                selectedColor = account.color.toComposeColor().takeIf {
                    selectedAccounts.data.contains(account)
                }
            ) { selected ->
                onItemClick(selected, account)
            }
        }
    }
}

@Composable
private fun SelectionBadges(
    text: String,
    icon: IvyIcon?,
    defaultIcon: IvyIcon = dummyIconSized(R.drawable.ic_custom_account_s),
    selectedColor: Color?,
    onClick: (selected: Boolean) -> Unit
) {
    val contrastColor = selectedColor?.contrast() ?: UI.colors.pureInverse

    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .thenIf(selectedColor == null) {
                border(2.dp, UI.colors.medium, UI.shapes.rFull)
            }
            .thenIf(selectedColor != null) {
                background(selectedColor!!, UI.shapes.rFull)
            }
            .clickable(
                onClick = {
                    onClick(selectedColor == null)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 12.dp)

        (icon ?: defaultIcon).ItemIcon(size = IconSize.S, contrastColor)

        SpacerHor(width = 4.dp)

        IvyText(
            modifier = Modifier.padding(vertical = 10.dp),
            text = text,
            typo = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        SpacerHor(width = 24.dp)
    }

    SpacerHor(width = 12.dp)
}

@Composable
private fun CategoriesFilter(
    allCategories: ImmutableData<List<Category>>,
    selectedCategories: ImmutableData<List<Category>>,
    onClearAll: () -> Unit,
    onSelectAll: () -> Unit,
    onItemClick: (Boolean, Category) -> Unit
) {
    LogCompositions(tag = TAG, msg = "Categories Filter")
    ListFilterTitle(
        text = stringResource(R.string.categories_number, selectedCategories.data.size),
        active = selectedCategories.data.isNotEmpty(),
        itemsSelected = selectedCategories.data.size,
        onClearAll = onClearAll,
        onSelectAll = onSelectAll
    )

    Spacer(Modifier.height(16.dp))

    LazyRow(
        modifier = Modifier.padding(start = 24.dp)
    ) {
        items(
            items = allCategories.data,
            key = {
                it.id.toString()
            }
        ) { category ->
            LogCompositions(tag = TAG, msg = "Categories Filter LazyColumn")
            SelectionBadges(
                icon = category.icon,
                text = category.name,
                selectedColor = category.color.toComposeColor().takeIf {
                    selectedCategories.data.contains(category)
                }
            ) {
                onItemClick(it, category)
            }
        }
    }
}

@Composable
private fun ListFilterTitle(
    text: String,
    active: Boolean,
    itemsSelected: Int,
    onClearAll: () -> Unit,
    onSelectAll: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterTitleText(
            text = text,
            active = active,
            inactiveColor = Red
        )

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable {
                    if (itemsSelected > 0) {
                        onClearAll()
                    } else {
                        onSelectAll()
                    }
                }
                .padding(all = 4.dp), //expand click area
            text = if (itemsSelected > 0) stringResource(com.ivy.base.R.string.clear_all) else stringResource(
                R.string.select_all
            ),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun AmountFilter(
    baseCurrency: String,
    minAmount: Double? = null,
    maxAmount: Double? = null,
    onClick: (AmountFilterType) -> Unit
) {
    LogCompositions(tag = TAG, msg = "Amount Filter")
    FilterTitleText(
        text = stringResource(R.string.amount_optional),
        active = minAmount != null || maxAmount != null
    )

    Spacer(Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(32.dp))

        Column(
            modifier = Modifier.clickable {
                onClick(AmountFilterType.MIN)
            },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.from),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            AmountCurrencyB1Row(
                amount = minAmount ?: 0.0,
                currency = baseCurrency
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.clickable {
                onClick(AmountFilterType.MAX)
            },
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(R.string.to),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            AmountCurrencyB1Row(
                amount = maxAmount ?: 0.0,
                currency = baseCurrency
            )
        }
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun KeywordsFilter(
    includedKeywords: ImmutableData<List<String>>,
    excludedKeywords: ImmutableData<List<String>>,
    onAdd: (KeywordsFilterType) -> Unit,
    onKeywordClick: (KeywordsFilterType, String) -> Unit
) {
    LogCompositions(tag = TAG, msg = "Keywords Filter")
    val localIncluded: List<Any> by remember(includedKeywords.data.size) {
        mutableStateOf(includedKeywords.data + listOf(AddKeywordButton()))
    }

    val localExcluded: List<Any> by remember(excludedKeywords.data.size) {
        mutableStateOf(excludedKeywords.data + listOf(AddKeywordButton()))
    }

    FilterTitleText(
        text = stringResource(R.string.keywords_optional),
        active = (includedKeywords.data.isNotEmpty() || excludedKeywords.data.isNotEmpty())
    )

    Spacer(Modifier.height(12.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = stringResource(R.string.includes_uppercase),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(12.dp))

    WrapContentRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        items = localIncluded
    ) { item ->
        LogCompositions(tag = TAG, msg = "Keywords Filter LazyRow Included")
        when (item) {
            is String -> {
                Keyword(
                    keyword = item,
                    borderColor = UI.colors.pureInverse
                ) {
                    onKeywordClick(KeywordsFilterType.INCLUDE, item)
                }
            }
            is AddKeywordButton -> {
                AddKeywordButton(text = stringResource(R.string.add_keyword)) {
                    onAdd(KeywordsFilterType.INCLUDE)
                }
            }
        }
    }

    Spacer(Modifier.height(20.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = stringResource(R.string.excludes_uppercase),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(12.dp))

    WrapContentRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        items = localExcluded
    ) { item ->
        LogCompositions(tag = TAG, msg = "Keywords Filter LazyRow Excluded")
        when (item) {
            is String -> {
                Keyword(
                    keyword = item,
                    borderColor = UI.colors.pureInverse
                ) {
                    onKeywordClick(KeywordsFilterType.EXCLUDE, item)
                }
            }
            is AddKeywordButton -> {
                AddKeywordButton(text = stringResource(R.string.add_keyword)) {
                    onAdd(KeywordsFilterType.EXCLUDE)
                }
            }
        }
    }

}

@Composable
private fun Keyword(
    keyword: String,
    borderColor: Color,
    onClick: () -> Unit,
) {
    IvyOutlinedButton(
        text = keyword,
        iconStart = R.drawable.ic_remove,
        iconTint = Red,
        borderColor = borderColor,
        padding = 10.dp,
    ) {
        onClick()
    }
}

@Composable
private fun AddKeywordButton(
    text: String,
    onCLick: () -> Unit
) {
    IvyOutlinedButton(
        text = text,
        iconStart = R.drawable.ic_plus,
        padding = 10.dp,
    ) {
        onCLick()
    }
}

@Composable
private fun FilterDivider() {
    Spacer(modifier = Modifier.height(24.dp))

    IvyDividerLine(
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))
}

private class AddKeywordButton