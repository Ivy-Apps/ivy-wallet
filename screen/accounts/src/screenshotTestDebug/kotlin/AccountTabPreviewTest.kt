@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.accounts.AccountsTabUICompactModeTest
import com.ivy.accounts.AccountsTabUITest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewAccountsTabLight() {
    AccountsTabUITest(dark = false)
}

@IvyPreviews
@Composable
private fun PreviewAccountsTabDark() {
    AccountsTabUITest(dark = true)
}

@IvyPreviews
@Composable
private fun PreviewAccountsTabCompactModeDark() {
    AccountsTabUICompactModeTest(dark = true)
}

@IvyPreviews
@Composable
private fun PreviewAccountsTabCompactModeLight() {
    AccountsTabUICompactModeTest(dark = false)
}
