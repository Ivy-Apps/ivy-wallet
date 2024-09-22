@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.transactions.TransactionsUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewTransactionsUiScreenLight() {
    TransactionsUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewTransactionsUiScreenDark() {
    TransactionsUiTest(isDark = true)
}