import androidx.compose.runtime.Composable
import com.ivy.accounts.AccountsTabUITest
import com.ivy.ui.testing.IvyPreviews

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


