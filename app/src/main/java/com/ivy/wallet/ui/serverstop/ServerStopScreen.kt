package com.ivy.wallet.ui.serverstop

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.Orange
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.theme.components.IvyButton

@Composable
fun ServerStopScreen() {
    val viewModel: ServerStopViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    UI(
        state = state,
        onBackup = {
            viewModel.exportToZip(context)
        }
    )
}

@Composable
private fun UI(
    state: ServerStopState,
    onBackup: () -> Unit,
) {
    ColumnRoot(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.exportInProgress) {
            Loading()
        } else {
            Content(
                onBackup = onBackup
            )
        }
    }
}

@Composable
private fun Content(
    onBackup: () -> Unit
) {
    SpacerVer(height = 24.dp)
    Text(
        text = "Backup your data",
        style = UI.typo.h2.style(color = UI.colors.primary)
    )
    SpacerVer(height = 16.dp)
    Text(
        text = "We're shutting down the Ivy Cloud servers on Jan 1st, 2023. " +
            "The cloud sync using Google account will no longer work!",
        style = UI.typo.b1.style(color = UI.colors.red)
    )
    SpacerVer(height = 12.dp)
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "The reason for that is because we're a FOSS (Free & Open Source Software) and " +
            "it isn't profitable for us to pay our servers monthly bills. More importantly, " +
            "we don't have a dedicated person to maintain them which is bad.",
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Normal,
        )
    )
    SpacerVer(height = 12.dp)
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "To keep your data safe in case of device change or app uninstall/clear data, " +
            "you need to make manually export a backup file of Ivy Wallet.",
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Bold,
        )
    )
    SpacerVer(height = 12.dp)
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "To export a backup file:\n" +
            "1) Click the arrow at top-right on the \"Home\" screen.\n" +
            "2) Go to \"Settings\"\n" +
            "3) Click \"Backup Data\"",
        style = UI.typo.b2.style(
            color = UI.colors.primary,
            fontWeight = FontWeight.Bold,
        )
    )
    SpacerVer(height = 12.dp)
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "To restore your data on Ivy Wallet fresh install, " +
            "you'll simply need to import the backup file that you've exported. " +
            "We recommend making regular exports and keeping the backup file on Google Drive, " +
            "OneDrive, DropBox or other safe cloud storage. Thank you for your understanding!",
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Normal,
        )
    )
    SpacerVer(height = 24.dp)
    IvyButton(text = "Export backup file") {
        onBackup()
    }
    SpacerVer(height = 24.dp)
}

@Composable
private fun ColumnScope.Loading() {
    SpacerWeight(weight = 1f)
    Text(
        text = "Exporting data...",
        style = UI.typo.b1.style(
            fontWeight = FontWeight.Bold,
            color = Orange
        )
    )
    SpacerWeight(weight = 1f)
}

// region Preview
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            state = ServerStopState(exportInProgress = false)
        ) {}
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    IvyWalletPreview {
        UI(
            state = ServerStopState(exportInProgress = true)
        ) {}
    }
}
// endregion
