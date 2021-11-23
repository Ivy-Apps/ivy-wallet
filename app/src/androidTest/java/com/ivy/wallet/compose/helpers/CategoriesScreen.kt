package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class CategoriesScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
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