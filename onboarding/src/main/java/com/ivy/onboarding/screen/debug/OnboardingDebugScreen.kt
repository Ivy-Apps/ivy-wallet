package com.ivy.onboarding.screen.debug

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.data.CurrencyCode
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton

@Composable
fun BoxScope.OnboardingDebug() {
    val viewModel: OnboardingDebugViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    ColumnRoot {
        SpacerVer(height = 24.dp)
        H1(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Onboarding"
        )
        SpacerWeight(weight = 1f)
        H2(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Base currency: ${state.baseCurrency}"
        )
        SpacerVer(height = 24.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CurrencyButton(
                currency = "BGN",
                baseCurrency = state.baseCurrency,
                onEvent = viewModel::onEvent
            )
            SpacerHor(width = 16.dp)
            CurrencyButton(
                currency = "USD",
                baseCurrency = state.baseCurrency,
                onEvent = viewModel::onEvent
            )
            SpacerHor(width = 16.dp)
            CurrencyButton(
                currency = "EUR",
                baseCurrency = state.baseCurrency,
                onEvent = viewModel::onEvent
            )
        }
        SpacerVer(height = 24.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Focused,
            feeling = Feeling.Positive,
            text = "Finish onboarding",
            icon = null
        ) {
            viewModel.onEvent(OnboardingDebugEvent.FinishOnboarding)
        }
        SpacerWeight(weight = 1f)
    }
}

@Composable
private fun CurrencyButton(
    currency: CurrencyCode,
    baseCurrency: CurrencyCode,
    onEvent: (OnboardingDebugEvent) -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = if (currency == baseCurrency)
            Visibility.High else Visibility.Medium,
        feeling = Feeling.Positive,
        text = currency,
        icon = null
    ) {
        onEvent(OnboardingDebugEvent.SetBaseCurrency(currency))
    }
}