import androidx.compose.runtime.Composable
import com.ivy.piechart.PieChartStatisticScreenUiTest
import com.ivy.ui.testing.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewPieChartStatisticsScreenLight() {
    PieChartStatisticScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewPieChartStatisticsScreenDark() {
    PieChartStatisticScreenUiTest(isDark = true)
}