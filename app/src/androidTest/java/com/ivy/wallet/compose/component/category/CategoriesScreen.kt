package com.ivy.wallet.compose.component.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.ItemStatisticScreen
import com.ivy.wallet.compose.component.ReorderModal

class CategoriesScreen(
    private val composeTestRule: IvyComposeTestRule
) {

    fun clickAddCategory(): CategoryModal {
        composeTestRule.onNodeWithText("Add category")
            .performClick()
        return CategoryModal(composeTestRule)
    }

    fun addCategory(
        categoryName: String,
        icon: String? = null,
        color: Color? = null,
    ): CategoriesScreen {
        return clickAddCategory()
            .enterTitle(categoryName)
            .apply {
                if (icon != null) {
                    chooseIcon(icon)
                }
            }
            .apply {
                if (color != null) {
                    chooseColor(color)
                }
            }
            .clickAdd(next = this)
            .assertCategory(categoryName = categoryName)
    }

    fun assertCategory(categoryName: String): CategoriesScreen {
        composeTestRule.onNode(hasText(categoryName))
            .performScrollTo()
            .assertIsDisplayed()

        return this
    }

    fun assertCategoryNotExists(categoryName: String): CategoriesScreen {
        composeTestRule.onNode(hasText(categoryName))
            .assertDoesNotExist()

        return this
    }

    fun clickCategory(categoryName: String): ItemStatisticScreen {
        composeTestRule.onNode(hasText(categoryName))
            .performScrollTo()
            .performClick()

        return ItemStatisticScreen(composeTestRule)
    }


    fun clickReorder(): ReorderModal {
        composeTestRule.onNodeWithTag("reorder_button")
            .performClick()

        return ReorderModal(composeTestRule)
    }
}