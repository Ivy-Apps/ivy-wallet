package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.CategoryModal
import com.ivy.wallet.compose.helpers.CategoryScreen
import com.ivy.wallet.compose.helpers.HomeMoreMenu
import com.ivy.wallet.compose.helpers.OnboardingFlow
import com.ivy.wallet.ui.theme.Blue
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class CategoriesTest : IvyComposeTest() {
    private val onboardingFlow = OnboardingFlow(composeTestRule)
    private val homeMoreMenu = HomeMoreMenu(composeTestRule)
    private val categoryModal = CategoryModal(composeTestRule)
    private val categoryScreen = CategoryScreen(composeTestRule)

    @Test
    fun CreateCategory() {
        onboardingFlow.onboardWith1AccountAnd1Category()

        homeMoreMenu.clickOpenCloseArrow()

        composeTestRule.onNodeWithText("Categories")
            .performClick()

        composeTestRule.onNodeWithText("Add category")
            .performClick()

        categoryModal.apply {
            enterTitle("Fun")
            chooseIconFlow.chooseIcon("game")
            colorPicker.chooseColor(Blue)

            clickAdd()
        }

        categoryScreen.assertCategory("Fun")
    }
}