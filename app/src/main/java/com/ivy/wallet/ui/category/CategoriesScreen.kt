package com.ivy.wallet.ui.category

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.Categories
import com.ivy.wallet.ui.ItemStatistic
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.ReorderButton
import com.ivy.wallet.ui.theme.components.ReorderModalSingleType
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import com.ivy.wallet.utils.balancePrefix
import com.ivy.wallet.utils.onScreenStart

@Composable
fun BoxWithConstraintsScope.CategoriesScreen(screen: Categories) {
    val viewModel: CategoriesViewModel = viewModel()

    val currency by viewModel.currency.collectAsState()
    val categories by viewModel.categories.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        currency = currency,
        categories = categories,

        onCreateCategory = viewModel::createCategory,
        onReorder = viewModel::reorder,
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    currency: String,
    categories: List<CategoryData>,

    onCreateCategory: (CreateCategoryData) -> Unit,
    onReorder: (List<CategoryData>) -> Unit,
) {
    var reorderVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
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

            ReorderButton {
                reorderVisible = true
            }

            Spacer(Modifier.width(24.dp))
        }


        Spacer(Modifier.height(16.dp))

        val nav = navigation()
        for (categoryData in categories) {
            CategoryCard(
                currency = currency,
                categoryData = categoryData,
                onLongClick = {
                    reorderVisible = true
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

        Spacer(Modifier.height(150.dp))  //scroll hack
    }

    val nav = navigation()
    CategoriesBottomBar(
        onAddCategory = {
            categoryModalData = CategoryModalData(
                category = null
            )
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = reorderVisible,
        initialItems = categories,
        dismiss = {
            reorderVisible = false
        },
        onReordered = onReorder
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
        modal = categoryModalData,
        onCreateCategory = onCreateCategory,
        onEditCategory = { },
        dismiss = {
            categoryModalData = null
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

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            currency = "BGN",
            categories = listOf(
                CategoryData(
                    category = Category(
                        "Groceries",
                        Green.toArgb(),
                        icon = "groceries"
                    ),
                    monthlyBalance = 2125.0,
                    monthlyExpenses = 920.0,
                    monthlyIncome = 3045.0
                ),
                CategoryData(
                    category = Category(
                        "Fun",
                        Orange.toArgb(),
                        icon = "game"
                    ),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 750.0,
                    monthlyIncome = 0.0
                ),
                CategoryData(
                    category = Category("Ivy", IvyDark.toArgb()),
                    monthlyBalance = 1200.0,
                    monthlyExpenses = 0.0,
                    monthlyIncome = 5000.0
                ),
                CategoryData(
                    category = Category(
                        "Food",
                        GreenLight.toArgb(),
                        icon = "atom"
                    ),
                    monthlyBalance = 12125.21,
                    monthlyExpenses = 1350.50,
                    monthlyIncome = 8000.48
                ),
                CategoryData(
                    category = Category(
                        "Shisha",
                        GreenDark.toArgb(),
                        icon = "drink"
                    ),
                    monthlyBalance = 820.0,
                    monthlyExpenses = 340.0,
                    monthlyIncome = 400.0
                ),

                ),

            onCreateCategory = { },
            onReorder = {},
        )
    }
}