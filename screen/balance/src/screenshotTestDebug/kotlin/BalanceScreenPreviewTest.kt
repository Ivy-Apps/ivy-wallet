import androidx.compose.runtime.Composable
import com.ivy.balance.BalanceScreenUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewBalanceScreenLight() {
    BalanceScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewBalanceScreenDark() {
    BalanceScreenUiTest(isDark = true)
}