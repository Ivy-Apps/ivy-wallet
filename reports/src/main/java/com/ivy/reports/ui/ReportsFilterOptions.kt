package com.ivy.reports.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    filter: ReportFilter?,
    onClose: () -> Unit,
    onSetFilter: (ReportFilter?) -> Unit
) {
    val modalId = remember { UUID.randomUUID() }
    val ivyContext = ivyWalletCtx()

    var localFilter by remember(filter) {
        mutableStateOf(filter)
    }

    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }

    var tp by remember(localFilter) { mutableStateOf(localFilter?.period) }

    val selectedTransactionTypes = remember(localFilter) {
        mutableStateListOf(*(localFilter?.trnTypes ?: emptyList()).toTypedArray())
    }
    val selectedAccounts = remember(localFilter) {
        mutableStateListOf(*(localFilter?.accounts ?: emptyList()).toTypedArray())
    }
    val selectedCategories = remember(localFilter) {
        mutableStateListOf(*(localFilter?.categories ?: emptyList()).toTypedArray())
    }

    val includedKeywords = remember(localFilter) {
        mutableStateListOf(*(localFilter?.includeKeywords ?: emptyList()).toTypedArray())
    }

    val excludeKeywords = remember(localFilter) {
        mutableStateListOf(*(localFilter?.excludeKeywords ?: emptyList()).toTypedArray())
    }

    var minAmount: Double? by remember(localFilter) {
        mutableStateOf(localFilter?.minAmount)
    }

    var maxAmount: Double? by remember(localFilter) {
        mutableStateOf(localFilter?.maxAmount)
    }

    val amountModalHandler = defaultExpandCollapseHandler()
    var amountModalState by remember {
        mutableStateOf(AmountModalState.empty())
    }

    val keywordModalHandler = defaultExpandCollapseHandler()
    var keywordModalAction: KeywordModalAction by remember {
        mutableStateOf(KeywordModalAction.IncludeKeyword)
    }

    val getReportFilter: () -> ReportFilter? = {
        val reportFilter = ReportFilter(
            trnTypes = selectedTransactionTypes,
            period = tp,
            accounts = selectedAccounts.toList(),
            categories = selectedCategories.toList(),
            currency = baseCurrency,
            minAmount = minAmount,
            maxAmount = maxAmount,
            includeKeywords = includedKeywords.toList(),
            excludeKeywords = excludeKeywords.toList()
        )
        if (reportFilter.hasEmptyContents())
            null
        else
            reportFilter
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ) {
                AddBackHandlingSupport(id = modalId, visible = visible, action = onClose)

                Spacer(Modifier.height(24.dp))

                FilterHeader(
                    onClearFilter = {
                        localFilter = ReportFilter.emptyFilter(baseCurrency = baseCurrency)
                    }
                )

                Spacer(Modifier.height(24.dp))

                TypeFilter(transactionTypes = selectedTransactionTypes)

                FilterDivider()

                PeriodFilter(tp) {
                    choosePeriodModal = ChoosePeriodModalData(
                        period = tp ?: ivyContext.selectedPeriod
                    )
                }

                FilterDivider()

                AccountsFilter(allAccounts = accounts, selectedAccounts = selectedAccounts)

                FilterDivider()

                CategoriesFilter(
                    allCategories = categories,
                    selectedCategories = selectedCategories
                )

                FilterDivider()

                AmountFilter(
                    baseCurrency = baseCurrency,
                    minAmount = minAmount,
                    maxAmount = maxAmount,
                    onShowMinAmountModal = {
                        amountModalState =
                            AmountModalState(amount = minAmount, onSetAmount = { minAmount = it })
                        amountModalHandler.expand()
                    }, onShowMaxAmountModal = {
                        amountModalState =
                            AmountModalState(amount = maxAmount, onSetAmount = { maxAmount = it })
                        amountModalHandler.expand()
                    }
                )

                FilterDivider()

                KeywordsFilter(
                    includedKeywords = includedKeywords,
                    excludedKeywords = excludeKeywords,
                    onShowIncludeKeywordModal = {
                        keywordModalAction = KeywordModalAction.IncludeKeyword
                        keywordModalHandler.expand()
                    },
                    onShowExcludeKeywordModal = {
                        keywordModalAction = KeywordModalAction.ExcludeKeyWord
                        keywordModalHandler.expand()
                    }
                )

                FilterFooter()
            }

            FilterOptions(
                onClose = onClose,
                onApplyFilter = {
                    onSetFilter(getReportFilter())
                    onClose()
                }
            )
        }
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = { choosePeriodModal = null },
    ) { selectedPeriod ->
        tp = selectedPeriod
    }

    AmountModal(
        id = UUID.randomUUID(),
        visible = amountModalHandler.expanded,
        currency = baseCurrency,
        initialAmount = amountModalState.amount,
        dismiss = {
            amountModalHandler.collapse()
        },
        onAmountChanged = amountModalState.onSetAmount
    )

    AddKeywordModal(
        keyword = "",
        visible = keywordModalHandler.expanded,
        dismiss = { keywordModalHandler.collapse() }
    ) { keyword ->
        val trimmedKeyword = keyword.trim()
        when (keywordModalAction) {
            is KeywordModalAction.IncludeKeyword -> if (trimmedKeyword !in includedKeywords)
                includedKeywords.add(trimmedKeyword)
            is KeywordModalAction.ExcludeKeyWord -> if (trimmedKeyword !in excludeKeywords)
                excludeKeywords.add(trimmedKeyword)
        }
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
private fun TypeFilter(transactionTypes: SnapshotStateList<TrnType>) {

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

        TypeFilterCheckboxNew(
            text = stringResource(id = R.string.income),
            checked = transactionTypes.contains(TrnType.INCOME)
        ) {
            if (it)
                transactionTypes.add(TrnType.INCOME)
            else
                transactionTypes.remove(TrnType.INCOME)
        }

        Spacer(Modifier.width(20.dp))

        TypeFilterCheckboxNew(
            text = stringResource(id = R.string.expense),
            checked = transactionTypes.contains(TrnType.EXPENSE)
        ) {
            if (it)
                transactionTypes.add(TrnType.EXPENSE)
            else
                transactionTypes.remove(TrnType.EXPENSE)
        }
    }

    Spacer(Modifier.height(4.dp))

    TypeFilterCheckboxNew(
        modifier = Modifier.padding(start = 20.dp),
        text = stringResource(id = R.string.transfer),
        checked = transactionTypes.contains(TrnType.TRANSFER)
    ) {
        if (it)
            transactionTypes.add(TrnType.TRANSFER)
        else
            transactionTypes.remove(TrnType.TRANSFER)
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

fun <T> springBounce(
    stiffness: Float = 500f,
) = spring<T>(
    dampingRatio = 0.75f,
    stiffness = stiffness,
)

@Composable
private fun TypeFilter(
    filter: ReportFilter?,
    nonNullFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    FilterTitleText(
        text = stringResource(com.ivy.base.R.string.by_type),
        active = filter != null && filter.trnTypes.isNotEmpty(),
        inactiveColor = Red
    )

    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        TypeFilterCheckbox(
            trnType = TrnType.INCOME,
            filter = filter,
            nonFilter = nonNullFilter,
            onSetFilter = onSetFilter
        )

        Spacer(Modifier.width(20.dp))

        TypeFilterCheckbox(
            trnType = TrnType.EXPENSE,
            filter = filter,
            nonFilter = nonNullFilter,
            onSetFilter = onSetFilter
        )
    }

    Spacer(Modifier.height(4.dp))

    TypeFilterCheckbox(
        modifier = Modifier.padding(start = 20.dp),
        trnType = TrnType.TRANSFER,
        filter = filter,
        nonFilter = nonNullFilter,
        onSetFilter = onSetFilter
    )
}


@Composable
private fun TypeFilterCheckboxNew(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    IvyCheckboxWithText(
        modifier = modifier,
        text = text,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}


@Composable
private fun TypeFilterCheckbox(
    modifier: Modifier = Modifier,
    trnType: TrnType,
    filter: ReportFilter?,
    nonFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    IvyCheckboxWithText(
        modifier = modifier,
        text = when (trnType) {
            TrnType.INCOME -> stringResource(com.ivy.base.R.string.incomes)
            TrnType.EXPENSE -> stringResource(com.ivy.base.R.string.expenses)
            TrnType.TRANSFER -> stringResource(com.ivy.base.R.string.account_transfers)
        },
        checked = filter != null && filter.trnTypes.contains(trnType),
    ) { checked ->
        if (checked) {
            //remove trn type
            onSetFilter(
                nonFilter(filter).copy(
                    trnTypes = nonFilter(filter).trnTypes.plus(trnType)
                )
            )
        } else {
            //add trn type
            onSetFilter(
                nonFilter(filter).copy(
                    trnTypes = nonFilter(filter).trnTypes.filter { it != trnType }
                )
            )
        }

    }
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
    selectedAccounts: SnapshotStateList<Account>
) {
    ListFilterTitle(
        text = stringResource(R.string.accounts_number, selectedAccounts.size),
        active = selectedAccounts.isNotEmpty(),
        itemsSelected = selectedAccounts.size,
        onClearAll = {
            selectedAccounts.clear()
        },
        onSelectAll = {
            selectedAccounts.addAll(allAccounts)
        }
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
                if (selected) {
                    selectedAccounts.add(account)
                } else {
                    selectedAccounts.remove(account)
                }
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
    Log.d("GGGG","Accounts\t"+text)
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
    selectedCategories: SnapshotStateList<Category>
) {

    ListFilterTitle(
        text = stringResource(R.string.categories_number, selectedCategories.size),
        active = selectedCategories.isNotEmpty(),
        itemsSelected = selectedCategories.size,
        onClearAll = {
            selectedCategories.clear()
        },
        onSelectAll = {
            selectedCategories.addAll(allCategories)
        }
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
            ) { selected ->
                if (selected) {
                    selectedCategories.add(category)
                } else {
                    selectedCategories.remove(category)
                }
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
                com.ivy.base.R.string.select_all
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

    onShowMinAmountModal: () -> Unit,
    onShowMaxAmountModal: () -> Unit,
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
                onShowMinAmountModal()
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
                onShowMaxAmountModal()
            },
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(com.ivy.base.R.string.to),
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
    includedKeywords: SnapshotStateList<String>,
    excludedKeywords: SnapshotStateList<String>,
    onShowIncludeKeywordModal: () -> Unit,
    onShowExcludeKeywordModal: () -> Unit,
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
                    includedKeywords.remove(item)
                }
            }
            is AddKeywordButton -> {
                AddKeywordButton(text = stringResource(R.string.add_keyword)) {
                    onShowIncludeKeywordModal()
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
                    excludedKeywords.remove(item)
                }
            }
            is AddKeywordButton -> {
                AddKeywordButton(text = stringResource(R.string.add_keyword)) {
                    onShowExcludeKeywordModal()
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
        iconStart = com.ivy.base.R.drawable.ic_remove,
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
        iconStart = com.ivy.base.R.drawable.ic_plus,
        padding = 10.dp,
    ) {
        onCLick()
    }
}

private class AddKeywordButton


@Composable
private fun FilterDivider() {
    Spacer(modifier = Modifier.height(24.dp))

    IvyDividerLine(
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))
}