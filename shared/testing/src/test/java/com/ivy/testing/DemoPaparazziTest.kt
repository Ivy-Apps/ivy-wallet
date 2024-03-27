package com.ivy.testing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DemoPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot demo composable`() {
        snapshot(theme) {
            DemoComposable()
        }
    }
}

@Composable
fun DemoComposable(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = "Demo composable",
        )
    }
}