package com.ivy.onboarding.screen.debug

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton

@Composable
fun BoxScope.OnboardingDebug() {
    val viewModel: OnboardingDebugViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val currencyPickerModal = rememberIvyModal()

    LaunchedEffect(Unit) {
        currencyPickerModal.show()
    }

    ColumnRoot {
        SpacerVer(height = 24.dp)
        H1(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Onboarding"
        )
        SpacerVer(height = 12.dp)
        Caption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Important: This is NOT a real onboarding," +
                    " it's just setup a quick hack for you to test the new Ivy Wallet app." +
                    " This screen is only for debugging purposes. It will be removed in production.",
            color = UI.colors.red
        )
        SpacerWeight(weight = 1f)
        H2(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Base currency: ${state.baseCurrency}"
        )
        SpacerVer(height = 24.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = if (state.baseCurrency.isNotBlank())
                Visibility.Medium else Visibility.High,
            feeling = Feeling.Positive,
            text = state.baseCurrency.takeIf { it.isNotBlank() } ?: "Pick one",
            onClick = {
                currencyPickerModal.show()
            }
        )
        SpacerVer(height = 24.dp)
        if (state.baseCurrency.isNotBlank()) {
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
        }
        SpacerWeight(weight = 1f)
    }

    CurrencyPickerModal(
        modal = currencyPickerModal,
        initialCurrency = state.baseCurrency,
        onCurrencyPick = {
            viewModel.onEvent(OnboardingDebugEvent.SetBaseCurrency(it))
        }
    )
}