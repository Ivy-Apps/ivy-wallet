import androidx.compose.runtime.Composable
import com.ivy.exchangerates.ExchangeRatesScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewExchangeRatesScreenLight() {
    ExchangeRatesScreenUiTest(isDark = false)
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewExchangeRatesScreenDark() {
    ExchangeRatesScreenUiTest(isDark = true)
}