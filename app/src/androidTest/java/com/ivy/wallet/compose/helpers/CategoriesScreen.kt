package com.ivy.wallet.compose.helpers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class CategoriesScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    private val categoryModal = CategoryModal(composeTestRule)

    fun addCategory(
        categoryName: String,
        icon: String? = null,
        color: Color? = null
    ) {
        composeTestRule.onNodeWithText("Add category")
            .performClick()

        categoryModal.apply {
            enterTitle(categoryName)
            if (icon != null) {
                chooseIconFlow.chooseIcon(icon)
            }
            if (color != null) {
                colorPicker.chooseColor(color)
            }

            clickAdd()
        }

        assertCategory(categoryName = categoryName)
    }

    fun assertCategory(categoryName: String) {
        composeTestRule.onNode(hasText(categoryName))
            .performScrollTo()
            .assertIsDisplayed()
    }

    fun assertCategoryNotExists(categoryName: String) {
        composeTestRule.onNode(hasText(categoryName))
            .assertDoesNotExist()
    }

    fun clickCategory(categoryName: String) {
        composeTestRule.onNode(hasText(categoryName))
            .performScrollTo()
            .performClick()
    }


    fun clickReorder() {
        composeTestRule.onNodeWithTag("reorder_button")
            .performClick()
    }
}