package com.ivy.wallet.ui.settings.experimental

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.frp.forward
import com.ivy.frp.then2
import com.ivy.frp.view.FRP
import com.ivy.frp.view.navigation.Screen
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.theme.components.IvySwitch

object ExperimentalScreen : Screen

@Composable
fun BoxWithConstraintsScope.ExperimentalScreen(screen: ExperimentalScreen) {
    FRP<ExpState, ExpEvent, ExperimentalViewModel>(
        initialEvent = ExpEvent.Load
    ) { state, onEvent ->
        UI(state, onEvent)
    }
}

@Composable
private fun UI(
    state: ExpState,

    onEvent: (ExpEvent) -> Unit
) {
    ColumnRoot(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        SpacerVer(height = 24.dp)

        IvyText(text = "Experimental", typo = UI.typo.h2)

        SpacerVer(height = 32.dp)

        when (state) {
            ExpState.Initial -> {}
            is ExpState.Loaded -> LoadedState(state = state, onEvent = onEvent)
        }
    }
}

@Composable
private fun LoadedState(
    state: ExpState.Loaded,
    onEvent: (ExpEvent) -> Unit
) {
    LazyColumn {
        item {
            BooleanPreference(
                name = "Smaller transactions",
                value = state.smallTrnsPref,
                onValueChanged = forward<Boolean>() then2
                        { ExpEvent.SetSmallTrnsPref(it) } then2 onEvent
            )
        }

        item {
            SpacerVer(height = 16.dp)

            BooleanPreference(
                name = "New edit screen",
                value = state.newEditScreen,
                onValueChanged = forward<Boolean>() then2
                        { ExpEvent.SetNewEditPref(it) } then2 onEvent
            )
        }
    }
}

@Composable
private fun BooleanPreference(
    name: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(UI.shapes.r2)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable {
                onValueChanged(!value)
            }
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 16.dp)

        IvyText(
            text = name,
            typo = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )

        SpacerWeight(weight = 1f)

        IvySwitch(
            enabled = value,
            onEnabledChange = forward<Boolean>() then2 onValueChanged
        )

        SpacerHor(width = 16.dp)
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            state = ExpState.Loaded(
                smallTrnsPref = false,
                newEditScreen = true
            ),
            onEvent = {}
        )
    }
}
