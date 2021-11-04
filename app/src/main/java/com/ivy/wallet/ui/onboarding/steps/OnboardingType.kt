package com.ivy.wallet.ui.onboarding.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.onboarding.components.OnboardingProgressSlider
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.components.IvyOutlinedButtonFillMaxWidth
import com.ivy.wallet.ui.theme.components.OnboardingButton

@Composable
fun OnboardingType(

    onStartImport: () -> Unit,
    onStartFresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(16.dp))

        val ivyContext = LocalIvyContext.current
        CloseButton(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            ivyContext.onBackPressed()
        }

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Import CSV file",
            style = Typo.h2.style(
                fontWeight = FontWeight.Black
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "from Ivy or another app",
            style = Typo.numberBody2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.weight(1f))

        Image(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.onboarding_illustration_import),
            contentDescription = "import illustration"
        )

        OnboardingProgressSlider(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            selectedStep = 0,
            stepsCount = 4,
            selectedColor = Orange
        )

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Importing a backup file from another can take up to 5 min. You can always import your data later if you want to.",
            style = Typo.body2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(24.dp))

        IvyOutlinedButtonFillMaxWidth(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = "Import backup file",
            iconStart = R.drawable.ic_export_csv,
            iconTint = Green,
            textColor = Green
        ) {
            onStartImport()
        }

        Spacer(Modifier.weight(1f))

        OnboardingButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            text = "Start fresh",
            textColor = White,
            backgroundGradient = GradientIvy,
            hasNext = true,
            enabled = true
        ) {
            onStartFresh()
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        OnboardingType(
            onStartImport = {}
        ) {

        }
    }
}