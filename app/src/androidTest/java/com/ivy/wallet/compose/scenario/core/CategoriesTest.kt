package com.ivy.wallet.compose.scenario.core

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.ItemStatisticScreen
import com.ivy.wallet.compose.component.category.CategoriesScreen
import com.ivy.wallet.compose.component.category.CategoryModal
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Blue2
import com.ivy.wallet.ui.theme.Ivy
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
class CategoriesTest : IvyComposeTest() {

    @Test
    fun CreateCategory() = testWithRetry {
        onboardWith1AccountAnd1Category()
            .openMoreMenu()
            .clickCategories()
            .clickAddCategory()
            .enterTitle("Fun")
            .chooseIcon("game")
            .chooseColor(Blue)
            .clickAdd(next = CategoriesScreen(composeTestRule))
            .assertCategory("Fun")
    }

    @Test
    fun AddSeveralCategories() = testWithRetry {
        onboardWith1AccountAnd1Category()
            .openMoreMenu()
            .clickCategories()
            .addCategory(
                categoryName = "Entertainment"
            )
            .addCategory(
                categoryName = "Bills",
                color = Blue2
            )
            .addCategory(
                categoryName = "Ivy",
                icon = "star"
            )
    }

    @Test
    fun EditCategory() = testWithRetry {
        onboardWith1AccountAnd1Category()
            .openMoreMenu()
            .clickCategories()
            .clickCategory(
                categoryName = "Food & Drinks"
            )
            .clickEdit(CategoryModal(composeTestRule))
            .enterTitle(
                title = "Eating"
            )
            .chooseIcon(
                icon = "restaurant"
            )
            .chooseColor(Ivy)
            .clickSave(next = ItemStatisticScreen(composeTestRule))
            .clickClose(next = CategoriesScreen(composeTestRule))
            .assertCategory(
                categoryName = "Eating"
            )
    }

    @Test
    fun DeleteCategory() = testWithRetry {
        onboardWith1AccountAnd1Category()
            .openMoreMenu()
            .clickCategories()

            .clickCategory(
                categoryName = "Food & Drinks"
            )
            .deleteItem(next = CategoriesScreen(composeTestRule))
            .assertCategoryNotExists(
                categoryName = "Food & Drinks"
            )
    }

    /**
     * semiTest because no actual reordering is being gone
     */
    @Ignore("Fails with very weird: java.lang.String com.ivy.wallet.domain.Settings.getCurrency()' on a null object reference")
    @Test
    fun ReorderCategories_semiTest() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickCategories()
            .clickReorder()
            .clickDone()
    }
}