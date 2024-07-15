import androidx.compose.runtime.Composable
import com.ivy.search.SearchUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewSearchUiScreenLight() {
    SearchUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewSearchUiScreenDark() {
    SearchUiTest(isDark = true)
}