package com.ivy.onboarding.steps

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.legacy.IvyWalletPreview
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.BackButton
import com.ivy.wallet.ui.theme.components.CurrencyPicker
import com.ivy.wallet.ui.theme.components.GradientCutBottom
import com.ivy.wallet.ui.theme.components.OnboardingButton
import com.ivy.legacy.utils.setStatusBarDarkTextCompat

@Composable
fun BoxWithConstraintsScope.OnboardingSetCurrency(
    preselectedCurrency: IvyCurrency,
    onSetCurrency: (IvyCurrency) -> Unit
) {
    setStatusBarDarkTextCompat(darkText = UI.colors.isLight)

    var currency by remember { mutableStateOf(preselectedCurrency) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Spacer(Modifier.height(16.dp))

        var keyboardVisible by remember {
            mutableStateOf(false)
        }

        val nav = navigation()
        BackButton(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            nav.onBackPressed()
        }

        if (!keyboardVisible) {
            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.set_currency),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        CurrencyPicker(
            modifier = Modifier
                .fillMaxSize(),
            initialSelectedCurrency = null,
            preselectedCurrency = preselectedCurrency,
            includeKeyboardShownInsetSpacer = true,
            lastItemSpacer = 120.dp,
            onKeyboardShown = { keyboardShown ->
                keyboardVisible = keyboardShown
            }
        ) {
            currency = it
        }
    }

    GradientCutBottom(
        height = 160.dp
    )

    OnboardingButton(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = 20.dp),

        text = stringResource(R.string.set),
        textColor = White,
        backgroundGradient = GradientIvy,
        hasNext = true,
        enabled = true
    ) {
        onSetCurrency(currency)
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        OnboardingSetCurrency(
            preselectedCurrency = IvyCurrency.getDefault()
        ) {
        }
    }
}
