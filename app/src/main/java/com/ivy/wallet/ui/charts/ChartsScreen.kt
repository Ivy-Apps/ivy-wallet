package com.ivy.wallet.ui.charts

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Screen

@Composable
fun BoxWithConstraintsScope.ChartsScreen(screen: Screen.Charts) {
    val viewModel: ChartsViewModel = viewModel()

    onScreenStart {
        viewModel.start()
    }
}

@Composable
private fun UI(

) {

}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI(

        )
    }
}