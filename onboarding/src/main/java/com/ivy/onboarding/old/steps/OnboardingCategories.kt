//package com.ivy.onboarding.steps
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.ivy.resources.R
//import com.ivy.data.CategoryOld
//import com.ivy.design.l0_system.UI
//import com.ivy.design.l0_system.style
//import com.ivy.design.util.IvyPreview
//
//import com.ivy.old.OnboardingProgressSlider
//import com.ivy.old.OnboardingToolbar
//import com.ivy.old.Suggestions
//import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
//import com.ivy.wallet.ui.theme.*
//import com.ivy.wallet.ui.theme.components.GradientCutBottom
//import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
//import com.ivy.wallet.ui.theme.components.OnboardingButton
//import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
//import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
//import com.ivy.wallet.utils.toLowerCaseLocal
//
//@ExperimentalFoundationApi
//@Composable
//fun BoxWithConstraintsScope.OnboardingCategories(
//    suggestions: List<CreateCategoryData>,
//    categories: List<CategoryOld>,
//
//    onCreateCategory: (CreateCategoryData) -> Unit = { },
//    onEditCategory: (CategoryOld) -> Unit = { _ -> },
//
//    onSkip: () -> Unit = {},
//    onDone: () -> Unit = {}
//) {
//    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
//
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .statusBarsPadding()
//            .navigationBarsPadding()
//    ) {
//        stickyHeader {
//            OnboardingToolbar(
//                hasSkip = categories.isEmpty(),
//                onBack = { nav.onBackPressed() },
//                onSkip = onSkip
//            )
//        }
//
//        item {
//            Column {
//                Spacer(Modifier.height(8.dp))
//
//                Text(
//                    modifier = Modifier.padding(horizontal = 32.dp),
//                    text = stringResource(R.string.add_categories),
//                    style = UI.typo.h2.style(
//                        fontWeight = FontWeight.Black
//                    )
//                )
//
////                PremiumInfo(
////                    itemLabelPlural = "categories",
////                    itemsCount = categories.size,
////                    freeItemsCount = Constants.FREE_CATEGORIES
////                )
//
//                if (categories.isEmpty()) {
//                    Spacer(Modifier.height(16.dp))
//
//                    Image(
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        painter = painterResource(id = R.drawable.onboarding_illustration_categories),
//                        contentDescription = "categories illustration"
//                    )
//
//                    OnboardingProgressSlider(
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        selectedStep = 3,
//                        stepsCount = 4,
//                        selectedColor = IvyDark
//                    )
//
//                    Spacer(Modifier.height(48.dp))
//                } else {
//                    Spacer(Modifier.height(24.dp))
//                }
//
//                Categories(
//                    categories = categories,
//                    onClick = {
//                        categoryModalData = CategoryModalData(
//                            category = it
//                        )
//                    }
//                )
//
//                if (categories.isNotEmpty()) {
//                    Spacer(Modifier.height(20.dp))
//                }
//
//                Text(
//                    modifier = Modifier.padding(horizontal = 32.dp),
//                    text = stringResource(R.string.suggestions),
//                    style = UI.typo.b1.style(
//                        fontWeight = FontWeight.ExtraBold
//                    )
//                )
//
//                Spacer(Modifier.height(16.dp))
//
//                Suggestions(
//                    suggestions = suggestions.filter { suggestion ->
//                        categories.map { it.name.toLowerCaseLocal() }
//                            .contains(suggestion.name.toLowerCaseLocal()).not()
//                    },
//                    onAddSuggestion = {
//                        onCreateCategory(it as CreateCategoryData)
//                    },
//                    onAddNew = {
//                        categoryModalData = CategoryModalData(
//                            category = null
//                        )
//                    }
//                )
//
//                Spacer(Modifier.height(96.dp))
//            }
//        }
//    }
//
//    GradientCutBottom(
//        height = 96.dp
//    )
//
//    if (categories.isNotEmpty()) {
//        OnboardingButton(
//            Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//                .align(Alignment.BottomCenter)
//                .navigationBarsPadding()
//                .padding(bottom = 20.dp),
//
//            text = stringResource(R.string.finish),
//            textColor = White,
//            backgroundGradient = GradientIvy,
//            hasNext = false,
//            enabled = true
//        ) {
//            onDone()
//        }
//    }
//
//    CategoryModal(
//        modal = categoryModalData,
//        onCreateCategory = onCreateCategory,
//        onEditCategory = onEditCategory,
//        dismiss = {
//            categoryModalData = null
//        }
//    )
//}
//
//
//@Composable
//private fun Categories(
//    categories: List<CategoryOld>,
//    onClick: (CategoryOld) -> Unit
//) {
//    for (category in categories) {
//        CategoryCard(
//            category = category
//        ) {
//            onClick(category)
//        }
//
//        Spacer(Modifier.height(8.dp))
//    }
//}
//
//@Composable
//private fun CategoryCard(
//    category: CategoryOld,
//    onClick: () -> Unit
//) {
//    val categoryColor = category.color.toComposeColor()
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
//            .clip(UI.shapes.rounded)
//            .background(UI.colors.medium, UI.shapes.rounded)
//            .clickable {
//                onClick()
//            },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Spacer(Modifier.width(20.dp))
//
//        ItemIconSDefaultIcon(
//            modifier = Modifier
//                .background(categoryColor, CircleShape),
//            iconName = category.icon,
//            defaultIcon = R.drawable.ic_custom_category_s,
//            tint = findContrastTextColor(categoryColor)
//        )
//
//        Spacer(Modifier.width(16.dp))
//
//        Text(
//            modifier = Modifier
//                .padding(start = 16.dp, end = 24.dp)
//                .padding(vertical = 24.dp),
//            text = category.name,
//            style = UI.typo.b2.style(
//                fontWeight = FontWeight.Bold
//            )
//        )
//
//        Spacer(Modifier.width(24.dp))
//    }
//}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview_Empty() {
//    IvyPreview {
//        OnboardingCategories(
//            suggestions = listOf(
//                CreateCategoryData(
//                    name = "Food & Drinks",
//                    color = Green,
//                    icon = "fooddrink"
//                ),
//
//                CreateCategoryData(
//                    name = "Bills & Fees",
//                    color = Red,
//                    icon = "bills"
//                ),
//
//                CreateCategoryData(
//                    name = "Transport",
//                    color = OrangeLight,
//                    icon = "transport"
//                ),
//
//                CreateCategoryData(
//                    name = "Groceries",
//                    color = Color(0xFF75ff4d),
//                    icon = "groceries"
//                ),
//
//                CreateCategoryData(
//                    name = "Entertainment",
//                    color = Orange,
//                    icon = "game"
//                ),
//
//                CreateCategoryData(
//                    name = "Shopping",
//                    color = Ivy,
//                    icon = "shopping"
//                ),
//
//                CreateCategoryData(
//                    name = "Gifts",
//                    color = RedLight,
//                    icon = "gift"
//                ),
//
//                CreateCategoryData(
//                    name = "Health",
//                    color = Color(0xFF4dfff3),
//                    icon = "health"
//                ),
//
//                CreateCategoryData(
//                    name = "Investments",
//                    color = Color(0xFF1e5166),
//                    icon = "leaf"
//                ),
//            ),
//            categories = listOf()
//        )
//    }
//}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview_Categories() {
//    IvyPreview {
//        OnboardingCategories(
//            suggestions = listOf(
//                CreateCategoryData(
//                    name = "Food & Drinks",
//                    color = Green,
//                    icon = "fooddrink"
//                ),
//
//                CreateCategoryData(
//                    name = "Bills & Fees",
//                    color = Red,
//                    icon = "bills"
//                ),
//
//                CreateCategoryData(
//                    name = "Transport",
//                    color = OrangeLight,
//                    icon = "transport"
//                ),
//
//                CreateCategoryData(
//                    name = "Groceries",
//                    color = Color(0xFF75ff4d),
//                    icon = "groceries"
//                ),
//
//                CreateCategoryData(
//                    name = "Entertainment",
//                    color = Orange,
//                    icon = "game"
//                ),
//
//                CreateCategoryData(
//                    name = "Shopping",
//                    color = Ivy,
//                    icon = "shopping"
//                ),
//
//                CreateCategoryData(
//                    name = "Gifts",
//                    color = RedLight,
//                    icon = "gift"
//                ),
//
//                CreateCategoryData(
//                    name = "Health",
//                    color = Color(0xFF4dfff3),
//                    icon = "health"
//                ),
//
//                CreateCategoryData(
//                    name = "Investments",
//                    color = Color(0xFF1e5166),
//                    icon = "leaf"
//                ),
//            ),
//            categories = listOf(
//                CategoryOld(
//                    name = "Food & Drinks",
//                    color = Orange.toArgb(),
//                    icon = "fooddrinks"
//                )
//            )
//        )
//    }
//}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview_Premium() {
//    IvyPreview {
//        OnboardingCategories(
//            suggestions = listOf(
//                CreateCategoryData(
//                    name = "Food & Drinks",
//                    color = Green,
//                    icon = "fooddrink"
//                ),
//
//                CreateCategoryData(
//                    name = "Bills & Fees",
//                    color = Red,
//                    icon = "bills"
//                ),
//
//                CreateCategoryData(
//                    name = "Transport",
//                    color = OrangeLight,
//                    icon = "transport"
//                ),
//
//                CreateCategoryData(
//                    name = "Groceries",
//                    color = Color(0xFF75ff4d),
//                    icon = "groceries"
//                ),
//
//                CreateCategoryData(
//                    name = "Entertainment",
//                    color = Orange,
//                    icon = "game"
//                ),
//
//                CreateCategoryData(
//                    name = "Shopping",
//                    color = Ivy,
//                    icon = "shopping"
//                ),
//
//                CreateCategoryData(
//                    name = "Gifts",
//                    color = RedLight,
//                    icon = "gift"
//                ),
//
//                CreateCategoryData(
//                    name = "Health",
//                    color = Color(0xFF4dfff3),
//                    icon = "health"
//                ),
//
//                CreateCategoryData(
//                    name = "Investments",
//                    color = Color(0xFF1e5166),
//                    icon = "leaf"
//                ),
//            ),
//            categories = List(12) {
//                CategoryOld(
//                    name = "Food & Drinks",
//                    color = Orange.toArgb(),
//                    icon = "fooddrinks"
//                )
//            }
//        )
//    }
//}