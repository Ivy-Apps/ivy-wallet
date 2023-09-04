package com.ivy.wallet.ui.donate

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.GradientOrange
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalTitle

@Composable
fun BoxWithConstraintsScope.DonateModal(
    visible: Boolean,
    dismiss: () -> Unit,
    onGooglePlay: () -> Unit,
    onGitHubSponsors: () -> Unit,
) {
    IvyModal(
        id = null,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {}
    ) {
        SpacerVer(height = 24.dp)
        ModalTitle(text = "Choose donation option")
        SpacerVer(height = 24.dp)
        IvyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Google PlayStore In-App purchase",
            backgroundGradient = Gradient.from(GradientOrange),
            hasGlow = false,
            wrapContentMode = false,
            iconStart = null
        ) {
            dismiss()
            onGooglePlay()
        }
        SpacerVer(height = 12.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "or",
            color = UI.colors.pureInverse,
            style = UI.typo.b1,
            textAlign = TextAlign.Center,
        )
        SpacerVer(height = 12.dp)
        IvyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "GitHub Sponsors",
            wrapContentMode = false,
            iconStart = R.drawable.github_logo
        ) {
            dismiss()
            onGitHubSponsors()
        }
        SpacerVer(height = 12.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = "*GitHub Sponsors is our preferred way of donation because:\n" +
                "1) You have custom donation options.\n2) If you choose a 'tier', you'll receive a small digital reward from us " +
                "as a token of gratitude!\n3) You can be publicly recognized as an Ivy Wallet sponsor.\n\n" +
                "Note: You'll need to choose your donation amount in the GitHub's website again.",
            color = UI.colors.primary,
            style = UI.typo.c,
            textAlign = TextAlign.Center,
        )
        SpacerVer(height = 8.dp)
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        DonateModal(
            visible = true,
            dismiss = {},
            onGooglePlay = {},
            onGitHubSponsors = {}
        )
    }
}
