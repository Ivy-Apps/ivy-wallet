@file:OptIn(ExperimentalFoundationApi::class)

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.ivy.transaction.EditTransactionScreenUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewEditTransactionScreenLight() {
    EditTransactionScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewEditTransactionScreenDark() {
    EditTransactionScreenUiTest(isDark = true)
}