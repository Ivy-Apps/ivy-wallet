package com.ivy.categories

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.R
import com.ivy.base.SortOrder
import com.ivy.data.CategoryOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.Categories
import com.ivy.screens.ItemStatistic
import com.ivy.wallet.ui.category.CategoriesScreenEvent
import com.ivy.wallet.ui.category.CategoriesScreenState
import com.ivy.wallet.ui.category.CategoriesViewModel
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import com.ivy.wallet.utils.balancePrefix
import java.util.*

@Composable
fun BoxWithConstraintsScope.CategoriesScreen(screen: Categories) {
    val viewModel: CategoriesViewModel = viewModel()
    val state by viewModel.state().collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: CategoriesScreenState = CategoriesScreenState(),
    onEvent: (CategoriesScreenEvent) -> Unit = {}
) {
    val nav = navigation()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        item {
            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))

                Text(
                    text = stringResource(R.string.categories),
                    style = UI.typo.h2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.weight(1f))

                CircleButtonFilled(
                    icon = R.drawable.ic_sort_by_alpha_24,
                    onClick = {
                        onEvent(
                            CategoriesScreenEvent.OnSortOrderModalVisible(
                                visible = true
                            )
                        )
                    },
                    clickAreaPadding = 12.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                ReorderButton {
                    onEvent(CategoriesScreenEvent.OnReorderModalVisible(true))
                }

                Spacer(Modifier.width(24.dp))
            }

            Spacer(Modifier.height(16.dp))
        }

        items(
            items = state.categories,
            key = { it.category.id }
        ) { categoryData ->
            CategoryCard(
                currency = state.baseCurrency,
                categoryData = categoryData,
                onLongClick = {
                    CategoriesScreenEvent.OnReorderModalVisible(true)
                }
            ) {
                nav.navigateTo(
                    ItemStatistic(
                        accountId = null,
                        categoryId = categoryData.category.id
                    )
                )
            }
        }

        item {
            Spacer(Modifier.height(150.dp))  //scroll hack
        }
    }
    CategoriesBottomBar(
        onAddCategory = {
            onEvent(
                CategoriesScreenEvent.OnCategoryModalVisible(
                    CategoryModalData(category = null)
                )
            )
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.categories,
        dismiss = {
            onEvent(CategoriesScreenEvent.OnReorderModalVisible(false))
        },
        onReordered = {
            onEvent(CategoriesScreenEvent.OnReorder(it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.category.name,
            style = UI.typo.b1.style(
                color = item.category.color.toComposeColor(),
                fontWeight = FontWeight.Bold
            )
        )
    }

    CategoryModal(
        modal = state.categoryModalData,
        parentCategoryList = state.parentCategoryList,
        onCreateCategory = {
            onEvent(CategoriesScreenEvent.OnCreateCategory(it))
        },
        onEditCategory = { },
        dismiss = {
            onEvent(CategoriesScreenEvent.OnCategoryModalVisible(null))
        }
    )

    SortModal(
        initialType = state.sortOrder,
        items = state.sortOrderItems,
        visible = state.sortModalVisible,
        dismiss = {
            onEvent(CategoriesScreenEvent.OnSortOrderModalVisible(visible = false))
        },
        onSortOrderChanged = {
            onEvent(CategoriesScreenEvent.OnReorder(state.categories, it))
        }
    )
}

@Composable
private fun CategoryCard(
    currency: String,
    categoryData: CategoryData,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val category = categoryData.category
    val contrastColor = findContrastTextColor(category.color.toComposeColor())

    Spacer(Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .clickable(
                onClick = onClick
            )
    ) {
        CategoryHeader(
            categoryData = categoryData,
            currency = currency,
            contrastColor = contrastColor
        )

        Spacer(Modifier.height(12.dp))

        AddedSpent(
            currency = currency,
            monthlyIncome = categoryData.monthlyIncome,
            monthlyExpenses = categoryData.monthlyExpenses
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun AddedSpent(
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    dividerColor: Color = UI.colors.medium,
    monthlyIncome: Double,
    monthlyExpenses: Double,
    currency: String,
    center: Boolean = true,
    dividerSpacer: Dp? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (center) {
            Spacer(Modifier.weight(1f))
        }

        LabelAmount(
            textColor = textColor,
            label = stringResource(R.string.month_expenses),
            amount = monthlyExpenses,
            currency = currency,
            center = center
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }

        if (dividerSpacer != null) {
            Spacer(modifier = Modifier.width(dividerSpacer))
        }

        //Divider
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(48.dp)
                .background(dividerColor, UI.shapes.rFull)
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }

        if (dividerSpacer != null) {
            Spacer(modifier = Modifier.width(dividerSpacer))
        }

        LabelAmount(
            textColor = textColor,
            label = stringResource(R.string.month_income),
            amount = monthlyIncome,
            currency = currency,
            center = center
        )

        if (center) {
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun LabelAmount(
    label: String,
    amount: Double,
    currency: String,
    textColor: Color,
    center: Boolean
) {
    Column(
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = label,
            style = UI.typo.c.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AmountCurrencyB1(
                textColor = textColor,
                amount = amount,
                currency = currency
            )
        }

    }

}

@Composable
private fun CategoryHeader(
    categoryData: CategoryData,
    currency: String,
    contrastColor: Color,
) {
    val category = categoryData.category

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(category.color.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = category.icon,
                defaultIcon = R.drawable.ic_custom_category_s,
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

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier.align(Alignment.CenterHorizontally),

            decimalPaddingTop = 4.dp,
            textColor = contrastColor,
            currency = currency,
            balance = categoryData.monthlyBalance,

            integerFontSize = 30.sp,
            decimalFontSize = 18.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false,
            balanceAmountPrefix = balancePrefix(
                income = categoryData.monthlyIncome,
                expenses = categoryData.monthlyExpenses
            )
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.SortModal(
    title: String = stringResource(R.string.sort_by),
    items: List<SortOrder>,
    visible: Boolean,
    initialType: SortOrder,
    id: UUID = UUID.randomUUID(),
    dismiss: () -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    var sortOrder by remember(initialType) {
        mutableStateOf(initialType)
    }

    val applyChange = {
        onSortOrderChanged(sortOrder)
        dismiss()
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                applyChange()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = title)

        Spacer(Modifier.height(32.dp))

        items.forEach {
            SelectTypeButton(
                text = it.displayName,
                icon = when (it) {
                    SortOrder.DEFAULT -> R.drawable.ic_custom_star_s
                    SortOrder.BALANCE_AMOUNT -> R.drawable.ic_vue_money_coins
                    SortOrder.EXPENSES -> R.drawable.ic_expense
                    SortOrder.ALPHABETICAL -> R.drawable.ic_sort_by_alpha_24
                },
                selected = it == sortOrder
            ) {
                sortOrder = it
                applyChange()
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SelectTypeButton(
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    selectedGradient: Gradient = GradientGreen,
    textSelectedColor: Color = White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(UI.shapes.r4)
            .background(
                brush = if (selected) selectedGradient.asHorizontalBrush() else SolidColor(UI.colors.medium),
                shape = UI.shapes.r4
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val textColor = if (selected) textSelectedColor else UI.colors.pureInverse

        IvyIcon(
            icon = icon,
            tint = textColor,
            modifier = Modifier.fillMaxHeight()
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier.wrapContentHeight(),
            text = text,
            style = UI.typo.b1.style(
                color = textColor
            ),
            textAlign = TextAlign.Center,
        )

        if (selected) {
            Spacer(Modifier.weight(1f))

            IvyIcon(
                icon = R.drawable.ic_check,
                tint = textSelectedColor
            )

            Text(
                text = stringResource(R.string.selected),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.SemiBold,
                    color = textSelectedColor
                )
            )

            Spacer(Modifier.width(24.dp))
        }
    }
}


@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        val state = CategoriesScreenState(
            baseCurrency = "BGN",
            categories = listOf(
                CategoryData(
                    category = CategoryOld(
                        "Groceries",
                        Green.toArgb(),
                        icon = "groceries"
                    ),
                    monthlyBalance = 2125.0,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                CategoryData(
                    category = CategoryOld(
                        "Fun",
                        Orange.toArgb(),
                        icon = "game"
                    ),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 0.0
                ),
                CategoryData(
                    category = CategoryOld("Ivy", IvyDark.toArgb()),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 0.0,
                    monthlyIncome = 5000.0
                ),
                CategoryData(
                    category = CategoryOld(
                        "Food",
                        GreenLight.toArgb(),
                        icon = "atom"
                    ),
                    monthlyBalance = 12125.21,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                CategoryData(
                    category = CategoryOld(
                        "Shisha",
                        GreenDark.toArgb(),
                        icon = "drink"
                    ),
                    monthlyBalance = 820.0,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),

                )
        )
        UI(state = state)
    }
}