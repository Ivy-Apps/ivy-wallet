import androidx.compose.runtime.Composable
import com.ivy.exchangerates.ExchangeRatesScreenUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewExchangeRatesScreenLight() {
    ExchangeRatesScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewExchangeRatesScreenDark() {
    ExchangeRatesScreenUiTest(isDark = true)
}