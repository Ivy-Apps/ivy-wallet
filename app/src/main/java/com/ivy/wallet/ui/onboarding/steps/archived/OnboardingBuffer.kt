package com.ivy.wallet.ui.onboarding.steps.archived

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.OnboardingButton
import com.ivy.wallet.ui.theme.modal.edit.AmountCurrency
import com.ivy.wallet.ui.theme.modal.edit.AmountInput

@Composable
fun OnboardingBuffer(
    currency: String,
    onBufferSet: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var amount by remember { mutableStateOf("") }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Savings Buffer",
            style = Typo.body1.style(
                fontWeight = FontWeight.ExtraBold,
                color = IvyTheme.colors.pureInverse
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 40.dp),
            text = "What's your savings goal?",
            style = Typo.body2.style(
                color = IvyTheme.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.weight(1f))

        AmountCurrency(
            amount = amount,
            currency = currency
        )

        Spacer(Modifier.height(32.dp))


        AmountInput(
            currency = currency,
            amount = amount
        ) {
            amount = it
        }

        Spacer(Modifier.weight(1f))

        OnboardingButton(
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            text = "Set",
            textColor = White,
            backgroundGradient = GradientIvy,
            hasNext = true,
            enabled = amount.toDoubleOrNull() != null
        ) {
            try {
                onBufferSet(amount.toDouble())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        OnboardingBuffer(
            currency = "BGN"
        ) {

        }
    }
}