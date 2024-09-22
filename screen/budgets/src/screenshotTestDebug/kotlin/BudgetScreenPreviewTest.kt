@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.budgets.BudgetScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewBudgetScreenLight() {
    BudgetScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewBudgetScreenDark() {
    BudgetScreenUiTest(isDark = true)
}