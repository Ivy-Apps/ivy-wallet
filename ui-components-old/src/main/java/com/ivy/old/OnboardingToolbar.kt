package com.ivy.old

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.base.R
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvyToolbar

@Composable
fun OnboardingToolbar(
    hasSkip: Boolean,

    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    IvyToolbar(onBack = onBack) {
        if (hasSkip) {
            Spacer(Modifier.weight(1f))

            Text(
                modifier = Modifier
                    .clip(UI.shapes.rFull)
                    .clickable {
                        onSkip()
                    }
                    .padding(all = 16.dp), //enlarge click area
                text = stringResource(R.string.skip),
                style = UI.typo.b2.style(
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.width(32.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletComponentPreview {
        OnboardingToolbar(
            hasSkip = true, onBack = {}
        ) {

        }
    }
}