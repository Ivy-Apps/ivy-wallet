@file:OptIn(ExperimentalFoundationApi::class)
@file:Suppress("UnusedPrivateMember")

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.ivy.transaction.EditTransactionScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

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