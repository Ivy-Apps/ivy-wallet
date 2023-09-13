package com.ivy.wallet.ui.applocked

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.resources.R
import com.ivy.legacy.IvyWalletPreview
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.legacy.utils.hasLockScreen

@Composable
fun BoxWithConstraintsScope.AppLockedScreen(
    onShowOSBiometricsModal: () -> Unit,
    onContinueWithoutAuthentication: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .background(UI.colors.medium, UI.shapes.rFull)
                .padding(vertical = 12.dp)
                .padding(horizontal = 32.dp),
            text = stringResource(R.string.app_locked),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
            )
        )

        Spacer(Modifier.weight(1f))

        Image(
            modifier = Modifier
                .size(width = 96.dp, height = 138.dp),
            painter = painterResource(id = R.drawable.ic_fingerprint),
            colorFilter = ColorFilter.tint(UI.colors.medium),
            contentScale = ContentScale.FillBounds,
            contentDescription = "unlock icon"
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.authenticate_text),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.SemiBold,
                color = Gray
            )
        )

        Spacer(Modifier.height(24.dp))

        val context = LocalContext.current
        IvyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.unlock),
            textStyle = UI.typo.b2.style(
                color = White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            wrapContentMode = false
        ) {
            osAuthentication(
                context = context,
                onShowOSBiometricsModal = onShowOSBiometricsModal,
                onContinueWithoutAuthentication = onContinueWithoutAuthentication
            )
        }
        Spacer(Modifier.height(24.dp))

        // To automatically launch the biometric screen on load of this composable
        LaunchedEffect(true) {
            osAuthentication(
                context = context,
                onShowOSBiometricsModal = onShowOSBiometricsModal,
                onContinueWithoutAuthentication = onContinueWithoutAuthentication
            )
        }
    }
}

private fun osAuthentication(
    context: Context,
    onShowOSBiometricsModal: () -> Unit,
    onContinueWithoutAuthentication: () -> Unit
) {
    if (hasLockScreen(context)) {
        onShowOSBiometricsModal()
    } else {
        onContinueWithoutAuthentication()
    }
}

@Preview
@Composable
private fun Preview_Locked() {
    IvyWalletPreview {
        AppLockedScreen(
            onContinueWithoutAuthentication = {},
            onShowOSBiometricsModal = {}
        )
    }
}
