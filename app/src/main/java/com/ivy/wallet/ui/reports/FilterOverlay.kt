package com.ivy.wallet.ui.reports

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.AddKeywordModal
import com.ivy.wallet.ui.theme.modal.AddModalBackHandling
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1Row
import com.ivy.wallet.utils.capitalizeLocal
import com.ivy.wallet.utils.springBounce
import com.ivy.wallet.utils.thenIf
import java.util.*
import kotlin.math.roundToInt

@Composable
fun BoxWithConstraintsScope.FilterOverlay(
    visible: Boolean,

    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    filter: ReportFilter?,
    onClose: () -> Unit,
    onSetFilter: (ReportFilter?) -> Unit
) {
    val percentVisible by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = springBounce()
    )

    var localFilter by remember(filter) {
        mutableStateOf(filter)
    }
    val setLocalFilter = { newFilter: ReportFilter ->
        localFilter = newFilter
    }
    val baseFilter = remember(baseCurrency) {
        ReportFilter.emptyFilter(baseCurrency = baseCurrency)
    }
    val nonNullFilter = { currentFilter: ReportFilter? ->
        ReportFilter
        currentFilter ?: baseFilter
    }

    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }
    var minAmountModalShown by remember { mutableStateOf(false) }
    var maxAmountModalShown by remember { mutableStateOf(false) }
    var includeKeywordModalShown by remember { mutableStateOf(false) }
    var excludeKeywordModalShown by remember { mutableStateOf(false) }

    if (percentVisible > 0.01f) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(
                        placeable.width, placeable.height
                    ) {
                        placeable.place(
                            x = 0,
                            y = -(placeable.height * (1f - percentVisible)).roundToInt()
                        )
                    }
                }
                .background(UI.colors.pure)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            val modalId = remember {
                UUID.randomUUID()
            }

            AddModalBackHandling(
                modalId = modalId,
                visible = visible
            ) {
                onClose()
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 32.dp
                    ),
                    text = stringResource(R.string.filter),
                    style = UI.typo.h2.style(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .clickable {
                            localFilter = null
                            onSetFilter(null)
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


            Spacer(Modifier.height(24.dp))

            TypeFilter(
                filter = localFilter,
                nonNullFilter = nonNullFilter,
                onSetFilter = setLocalFilter
            )

            FilterDivider()

            val ivyContext = ivyWalletCtx()
            PeriodFilter(
                filter = localFilter,
                onShowPeriodChooserModal = {
                    choosePeriodModal = ChoosePeriodModalData(
                        period = filter?.period ?: ivyContext.selectedPeriod
                    )
                }
            )

            FilterDivider()

            AccountsFilter(
                allAccounts = accounts,
                filter = localFilter,
                nonNullFilter = nonNullFilter,
                onSetFilter = setLocalFilter
            )

            FilterDivider()

            CategoriesFilter(
                allCategories = categories,
                filter = localFilter,
                nonNullFilter = nonNullFilter,
                onSetFilter = setLocalFilter
            )

            FilterDivider()

            AmountFilter(
                baseCurrency = baseCurrency,
                filter = localFilter,
                onShowMinAmountModal = {
                    minAmountModalShown = true
                },
                onShowMaxAmountModal = {
                    maxAmountModalShown = true
                }
            )

            FilterDivider()

            KeywordsFilter(
                filter = localFilter,
                onSetFilter = setLocalFilter,
                nonNullFilter = nonNullFilter,
                onShowIncludeKeywordModal = {
                    includeKeywordModalShown = true
                },
                onShowExcludeKeywordModal = {
                    excludeKeywordModalShown = true
                }
            )

            Spacer(Modifier.height(196.dp))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(percentVisible)
            .align(Alignment.BottomCenter)
            .zIndex(200f)
            .padding(bottom = 32.dp)
    ) {
        Spacer(Modifier.width(24.dp))

        CloseButton {
            onClose()
        }

        Spacer(Modifier.weight(1f))

        IvyButton(
            text = stringResource(R.string.apply_filter),
            iconStart = R.drawable.ic_filter_xs,
            backgroundGradient = GradientGreen,
            padding = 10.dp,
        ) {
            if (localFilter != null) {
                onSetFilter(localFilter!!)
            }
            onClose()
        }

        Spacer(Modifier.width(24.dp))
    }

    if (percentVisible > 0.01f) {
        GradientCutBottom(
            height = 196.dp,
            alpha = percentVisible,
            zIndex = 150f
        )
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = { choosePeriodModal = null },
    ) { selectedPeriod ->
        localFilter = nonNullFilter(localFilter).copy(
            period = selectedPeriod
        )
    }

    val minAmountModalId = remember(
        nonNullFilter(filter).id,
        nonNullFilter(filter).minAmount
    ) {
        UUID.randomUUID()
    }
    AmountModal(
        id = minAmountModalId,
        visible = minAmountModalShown,
        currency = baseCurrency,
        initialAmount = filter?.minAmount?.takeIf { it > 0 },
        dismiss = {
            minAmountModalShown = false
        }
    ) {
        localFilter = nonNullFilter(localFilter).copy(
            minAmount = it.takeIf { it > 0 }
        )
    }

    val maxAmountModalId = remember(
        nonNullFilter(localFilter).id,
        nonNullFilter(localFilter).maxAmount
    ) {
        UUID.randomUUID()
    }
    AmountModal(
        id = maxAmountModalId,
        visible = maxAmountModalShown,
        currency = baseCurrency,
        initialAmount = filter?.maxAmount?.takeIf { it > 0 },
        dismiss = {
            maxAmountModalShown = false
        }
    ) {
        localFilter = nonNullFilter(localFilter).copy(
            maxAmount = it.takeIf { it > 0 }
        )
    }

    AddKeywordModal(
        keyword = "",
        visible = includeKeywordModalShown,
        dismiss = { includeKeywordModalShown = false }
    ) { keyword ->
        localFilter = nonNullFilter(localFilter).copy(
            includeKeywords = nonNullFilter(localFilter)
                .includeKeywords.plus(keyword)
                .toSet().toList() //filter duplicated
        )
    }

    AddKeywordModal(
        keyword = "",
        visible = excludeKeywordModalShown,
        dismiss = { excludeKeywordModalShown = false }
    ) { keyword ->
        localFilter = nonNullFilter(localFilter).copy(
            excludeKeywords = nonNullFilter(localFilter)
                .excludeKeywords.plus(keyword)
                .toSet().toList() //filter duplicated
        )
    }
}

@Composable
private fun TypeFilter(
    filter: ReportFilter?,
    nonNullFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    FilterTitleText(
        text = stringResource(R.string.by_type),
        active = filter != null && filter.trnTypes.isNotEmpty(),
        inactiveColor = Red
    )

    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        TypeFilterCheckbox(
            trnType = TransactionType.INCOME,
            filter = filter,
            nonFilter = nonNullFilter,
            onSetFilter = onSetFilter
        )

        Spacer(Modifier.width(20.dp))

        TypeFilterCheckbox(
            trnType = TransactionType.EXPENSE,
            filter = filter,
            nonFilter = nonNullFilter,
            onSetFilter = onSetFilter
        )
    }

    Spacer(Modifier.height(4.dp))

    TypeFilterCheckbox(
        modifier = Modifier.padding(start = 20.dp),
        trnType = TransactionType.TRANSFER,
        filter = filter,
        nonFilter = nonNullFilter,
        onSetFilter = onSetFilter
    )
}

@Composable
private fun TypeFilterCheckbox(
    modifier: Modifier = Modifier,
    trnType: TransactionType,
    filter: ReportFilter?,
    nonFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    IvyCheckboxWithText(
        modifier = modifier,
        text = when (trnType) {
            TransactionType.INCOME -> stringResource(R.string.incomes)
            TransactionType.EXPENSE -> stringResource(R.string.expenses)
            TransactionType.TRANSFER -> stringResource(R.string.account_transfers)
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
    filter: ReportFilter?,
    onShowPeriodChooserModal: () -> Unit
) {
    FilterTitleText(
        text = stringResource(R.string.time_period),
        active = filter?.period != null,
        inactiveColor = Red
    )

    Spacer(Modifier.height(16.dp))

    IvyOutlinedButtonFillMaxWidth(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        iconStart = R.drawable.ic_calendar,
        text = filter?.period?.toDisplayLong(ivyWalletCtx().startDayOfMonth)
            ?.capitalizeLocal()
            ?: stringResource(R.string.select_time_range),
        padding = 12.dp,
    ) {
        onShowPeriodChooserModal()
    }
}

@Composable
private fun AccountsFilter(
    allAccounts: List<Account>,
    filter: ReportFilter?,
    nonNullFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    ListFilterTitle(
        text = stringResource(R.string.accounts_number, filter?.accounts?.size ?: 0),
        active = filter != null && filter.accounts.isNotEmpty(),
        itemsSelected = filter?.accounts?.size ?: 0,
        onClearAll = {
            onSetFilter(
                nonNullFilter(filter).copy(
                    accounts = emptyList()
                )
            )
        },
        onSelectAll = {
            onSetFilter(
                nonNullFilter(filter).copy(
                    accounts = allAccounts
                )
            )
        }
    )

    Spacer(Modifier.height(16.dp))

    LazyRow {
        item {
            Spacer(Modifier.width(24.dp))
        }

        items(items = allAccounts) { account ->
            ListItem(
                icon = account.icon,
                defaultIcon = R.drawable.ic_custom_account_s,
                text = account.name,
                selectedColor = account.color.toComposeColor().takeIf {
                    filter?.accounts?.contains(account) == true
                }
            ) { selected ->
                if (selected) {
                    //remove account
                    onSetFilter(
                        nonNullFilter(filter).copy(
                            accounts = nonNullFilter(filter).accounts.filter { it != account }
                        )
                    )
                } else {
                    //add account
                    onSetFilter(
                        nonNullFilter(filter).copy(
                            accounts = nonNullFilter(filter).accounts
                                .plus(account).sortedBy { it.orderNum }
                        )
                    )
                }
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
private fun CategoriesFilter(
    allCategories: List<Category>,
    filter: ReportFilter?,
    nonNullFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit
) {
    val myNonNullFilter = nonNullFilter(filter)
    val selectedItemsCount = filter?.categories?.size ?: 0

    ListFilterTitle(
        text = stringResource(R.string.categories_number, selectedItemsCount),
        active = filter != null && filter.categories.isNotEmpty(),
        itemsSelected = selectedItemsCount,
        onClearAll = {
            onSetFilter(
                myNonNullFilter.copy(
                    categories = emptyList()
                )
            )
        },
        onSelectAll = {
            onSetFilter(
                myNonNullFilter.copy(
                    categories = allCategories
                )
            )
        }
    )

    Spacer(Modifier.height(16.dp))

    LazyRow {
        item {
            Spacer(Modifier.width(24.dp))
        }

        items(items = allCategories) { category ->
            ListItem(
                icon = category.icon,
                defaultIcon = R.drawable.ic_custom_category_s,
                text = category.name,
                selectedColor = category.color.toComposeColor().takeIf {
                    filter?.categories?.contains(category) == true
                }
            ) { selected ->
                if (selected) {
                    //remove category
                    onSetFilter(
                        myNonNullFilter.copy(
                            categories = myNonNullFilter.categories.filter { it != category }
                        )
                    )
                } else {
                    //add category
                    onSetFilter(
                        myNonNullFilter.copy(
                            categories = myNonNullFilter.categories
                                .plus(category).sortedBy { it.orderNum }
                        )
                    )
                }
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
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
            text = if (itemsSelected > 0) stringResource(R.string.clear_all) else stringResource(R.string.select_all),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )

        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
fun ListItem(
    icon: String?,
    @DrawableRes defaultIcon: Int,
    text: String,
    selectedColor: Color?,
    onClick: (selected: Boolean) -> Unit
) {
    val textColor =
        if (selectedColor != null) findContrastTextColor(selectedColor) else UI.colors.pureInverse

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
                    onClick(selectedColor != null)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            iconName = icon,
            defaultIcon = defaultIcon,
            tint = textColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(12.dp))
}

@Composable
private fun AmountFilter(
    baseCurrency: String,
    filter: ReportFilter?,

    onShowMinAmountModal: () -> Unit,
    onShowMaxAmountModal: () -> Unit,
) {
    FilterTitleText(
        text = stringResource(R.string.amount_optional),
        active = filter?.minAmount != null || filter?.maxAmount != null
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
                amount = filter?.minAmount ?: 0.0,
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
                text = stringResource(R.string.to),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            AmountCurrencyB1Row(
                amount = filter?.maxAmount ?: 0.0,
                currency = baseCurrency
            )
        }
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun KeywordsFilter(
    filter: ReportFilter?,
    nonNullFilter: (ReportFilter?) -> ReportFilter,
    onSetFilter: (ReportFilter) -> Unit,

    onShowIncludeKeywordModal: () -> Unit,
    onShowExcludeKeywordModal: () -> Unit,
) {
    FilterTitleText(
        text = stringResource(R.string.keywords_optional),
        active = filter != null &&
            (filter.includeKeywords.isNotEmpty() || filter.excludeKeywords.isNotEmpty())
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

    val includes = nonNullFilter(filter).includeKeywords + listOf(AddKeywordButton())
    WrapContentRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        items = includes
    ) { item ->
        when (item) {
            is String -> {
                Keyword(
                    keyword = item,
                    borderColor = UI.colors.pureInverse
                ) {
                    //Remove keyword
                    onSetFilter(
                        nonNullFilter(filter).copy(
                            includeKeywords = nonNullFilter(filter)
                                .includeKeywords.filter { it != item }
                        )
                    )
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

    val excludes = nonNullFilter(filter).excludeKeywords + listOf(AddKeywordButton())
    WrapContentRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        items = excludes
    ) { item ->
        when (item) {
            is String -> {
                Keyword(
                    keyword = item,
                    borderColor = UI.colors.pureInverse
                ) {
                    //Remove keyword
                    onSetFilter(
                        nonNullFilter(filter).copy(
                            excludeKeywords = nonNullFilter(filter)
                                .excludeKeywords.filter { it != item }
                        )
                    )
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

private class AddKeywordButton


@Composable
private fun FilterDivider() {
    Spacer(modifier = Modifier.height(24.dp))

    IvyDividerLine(
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))
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

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")

        FilterOverlay(
            visible = true,

            baseCurrency = "BGN",
            accounts = listOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = listOf(
                cat1,
                Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                Category("Home", color = Green.toArgb(), icon = null),
            ),

            filter = ReportFilter.emptyFilter("BGN").copy(
                accounts = listOf(
                    acc1, acc2
                ),
                categories = listOf(
                    cat1
                ),
                minAmount = null,
                maxAmount = 13256.27,
            ),
            onClose = { },
            onSetFilter = {
            }
        )
    }
}