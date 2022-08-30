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
    visible: Boolean,
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,
    filter: ReportFilter,
    onClose: () -> Unit,
    onFilterEvent: (ReportFilterEvent) -> Unit
) {
    val ivyContext = ivyWalletCtx()
    val modalId = remember { UUID.randomUUID() }

    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }

    val amtFilterHandler = defaultExpandCollapseHandler()
    var amtFilterType: AmountFilterType by remember {
        mutableStateOf(AmountFilterType.MIN)  //Default value
    }

    val keywordFilterHandler = defaultExpandCollapseHandler()
    var keywordsFilterType: KeywordsFilterType by remember {
        mutableStateOf(KeywordsFilterType.INCLUDE)
    }

    AnimatedVisibility(
        visible = visible,
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
                    AddBackHandlingSupport(id = modalId, visible = visible, action = onClose)

                    Spacer(Modifier.height(24.dp))
                }

                item {
                    FilterHeader(
                        onClearFilter = {
                            onFilterEvent(ReportFilterEvent.Clear(ClearType.ALL))
                        }
                    )

                    Spacer(Modifier.height(24.dp))
                }

                item {
                    TypeFilter(transactionTypes = filter.trnTypes) { checked, trnType ->
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
                    PeriodFilter(filter.period) {
                        choosePeriodModal = ChoosePeriodModalData(
                            period = filter.period ?: ivyContext.selectedPeriod
                        )
                    }

                    FilterDivider()
                }

                item {
                    AccountsFilter(
                        allAccounts = accounts,
                        selectedAccounts = filter.accounts,
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
                        allCategories = categories,
                        selectedCategories = filter.categories,
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
                        minAmount = filter.minAmount,
                        maxAmount = filter.maxAmount
                    ) { filterType ->
                        amtFilterType = filterType
                        amtFilterHandler.expand()
                    }

                    FilterDivider()
                }

                item {
                    KeywordsFilter(
                        includedKeywords = filter.includeKeywords,
                        excludedKeywords = filter.excludeKeywords,
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
                    onFilterEvent(ReportFilterEvent.FilterSet(filter))
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
            AmountFilterType.MIN -> filter.minAmount
            AmountFilterType.MAX -> filter.maxAmount
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
private fun FilterHeader(onClearFilter: () -> Unit) {
    Row(
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
    transactionTypes: List<TrnType>,
    onChecked: (Boolean, TrnType) -> Unit
) {
    FilterTitleText(
        text = stringResource(R.string.by_type),
        active = transactionTypes.isNotEmpty(),
        inactiveColor = Red
    )

    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyCheckboxWithText(
            text = stringResource(id = R.string.income),
            checked = transactionTypes.contains(TrnType.INCOME)
        ) {
            onChecked(it, TrnType.INCOME)
        }

        Spacer(Modifier.width(20.dp))

        IvyCheckboxWithText(
            text = stringResource(id = R.string.expense),
            checked = transactionTypes.contains(TrnType.EXPENSE)
        ) {
            onChecked(it, TrnType.EXPENSE)
        }
    }

    Spacer(Modifier.height(4.dp))

    IvyCheckboxWithText(
        modifier = Modifier.padding(start = 20.dp),
        text = stringResource(id = R.string.transfer),
        checked = transactionTypes.contains(TrnType.TRANSFER)
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
    period: TimePeriod?,
    onShowPeriodChooserModal: () -> Unit
) {
    val ctx = ivyWalletCtx()
    val defaultText = stringResource(R.string.select_time_range)
    val text by remember(period) {
        mutableStateOf(
            period?.toDisplayLong(ctx.startDayOfMonth)
                ?.capitalizeLocal()
                ?: defaultText
        )
    }

    FilterTitleText(
        text = stringResource(R.string.time_period),
        active = period != null,
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
    allAccounts: List<Account>,
    selectedAccounts: List<Account>,
    onClearAll: () -> Unit,
    onSelectAll: () -> Unit,
    onItemClick: (Boolean, Account) -> Unit
) {
    ListFilterTitle(
        text = stringResource(R.string.accounts_number, selectedAccounts.size),
        active = selectedAccounts.isNotEmpty(),
        itemsSelected = selectedAccounts.size,
        onClearAll = onClearAll,
        onSelectAll = onSelectAll
    )

    Spacer(Modifier.height(16.dp))

    LazyRow(modifier = Modifier.padding(start = 24.dp)) {
        items(
            items = allAccounts,
            key = {
                it.id.toString()
            }
        ) { account ->
            SelectionBadges(
                text = account.name,
                icon = account.icon,
                selectedColor = account.color.toComposeColor().takeIf {
                    selectedAccounts.contains(account)
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
    allCategories: List<Category>,
    selectedCategories: List<Category>,
    onClearAll: () -> Unit,
    onSelectAll: () -> Unit,
    onItemClick: (Boolean, Category) -> Unit
) {
    ListFilterTitle(
        text = stringResource(R.string.categories_number, selectedCategories.size),
        active = selectedCategories.isNotEmpty(),
        itemsSelected = selectedCategories.size,
        onClearAll = onClearAll,
        onSelectAll = onSelectAll
    )

    Spacer(Modifier.height(16.dp))

    LazyRow(
        modifier = Modifier.padding(start = 24.dp)
    ) {
        items(
            items = allCategories,
            key = {
                it.id.toString()
            }
        ) { category ->
            SelectionBadges(
                icon = category.icon,
                text = category.name,
                selectedColor = category.color.toComposeColor().takeIf {
                    selectedCategories.contains(category)
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
    includedKeywords: List<String>,
    excludedKeywords: List<String>,
    onAdd: (KeywordsFilterType) -> Unit,
    onKeywordClick: (KeywordsFilterType, String) -> Unit
) {
    val localIncluded: List<Any> by remember(includedKeywords.size) {
        mutableStateOf(includedKeywords + listOf(AddKeywordButton()))
    }

    val localExcluded: List<Any> by remember(excludedKeywords.size) {
        mutableStateOf(excludedKeywords + listOf(AddKeywordButton()))
    }

    FilterTitleText(
        text = stringResource(R.string.keywords_optional),
        active = (includedKeywords.isNotEmpty() || excludedKeywords.isNotEmpty())
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

private fun <T> MutableList<T>.addOrRemove(add: Boolean, item: T) {
    if (add)
        this.add(item)
    else
        this.remove(item)
}

private class AddKeywordButton