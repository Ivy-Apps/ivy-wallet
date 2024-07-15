import androidx.compose.runtime.Composable
import com.ivy.attributions.AttributionScreenUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun AttributionsUILight() {
    AttributionScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun AttributionsUIDark() {
    AttributionScreenUiTest(isDark = true)
}