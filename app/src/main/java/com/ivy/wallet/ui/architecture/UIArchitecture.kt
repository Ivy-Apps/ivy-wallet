package com.ivy.wallet.ui.architecture

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.utils.onScreenStart

@Composable
inline fun <S, E, reified VM : FRPViewModel<S, E>> BoxWithConstraintsScope.FRP(
    initialEvent: E? = null,
    UI: @Composable BoxWithConstraintsScope.(
        state: S,
        onEvent: (E) -> Unit
    ) -> Unit
) {
    val viewModel: VM = viewModel()
    val state by viewModel.state().collectAsState()

    if (initialEvent != null) {
        onScreenStart {
            viewModel.onEvent(initialEvent)
        }
    }

    UI(state, viewModel::onEvent)
}