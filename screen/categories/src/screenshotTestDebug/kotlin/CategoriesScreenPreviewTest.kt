@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.categories.CategoriesScreenCompactUiTest
import com.ivy.categories.CategoriesScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewCategoriesScreenLight() {
    CategoriesScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewCategoriesScreenDark() {
    CategoriesScreenUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewCategoriesScreenCompactLight() {
    CategoriesScreenCompactUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewCategoriesScreenCompactDark() {
    CategoriesScreenCompactUiTest(isDark = true)
}